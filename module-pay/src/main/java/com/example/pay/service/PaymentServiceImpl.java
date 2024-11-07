package com.example.pay.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.pay.config.TossPaymentConfig;
import com.example.pay.domain.PaymentEntity;
import com.example.pay.dto.Payment;
import com.example.pay.exception.PaymentException;
import com.example.pay.repository.PaymentRepository;
import com.example.pay.type.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentEventPublisher eventPublisher;
	private final TossPaymentConfig tossPaymentConfig;
	private final RestTemplate restTemplate;
	private final PaymentRepository paymentRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String PAYMENT_CACHE_KEY_PREFIX = "payment:";
	private static final String TRANSACTION_ID = "transactionId";

	@Override
	@Transactional
	public Payment.Response confirmPayment(Payment.Request request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put(TRANSACTION_ID, transactionId);
		log.info("결제 확인 요청 시작: transactionId={}, request={}", transactionId, request);
		long startTime = System.currentTimeMillis();

		try {
			validatePaymentInfo(request);
			checkExistingPayment(request.getOrderId());

			Payment.Response responseBody = callTossPaymentApi(request);
			log.info("결제 확인 성공: transactionId={}, response={}", transactionId, responseBody);

			savePaymentInfo(Objects.requireNonNull(responseBody));
			invalidateCache(request.getOrderId(), request.getPaymentKey());

			Payment.PaymentEventDto eventDto = Payment.PaymentEventDto.from(responseBody);
			eventPublisher.publishPaymentCompleted(eventDto);
			log.info("결제 상태 변경 이벤트 발행 완료: status={}", responseBody.getStatus());

			return responseBody;
		} catch (Exception e) {
			Payment.PaymentFailureEvent failureEvent = Payment.PaymentFailureEvent.builder()
				.orderId(request.getOrderId())
				.paymentKey(request.getPaymentKey())
				.errorCode("PAYMENT_FAILED")
				.errorMessage(e.getMessage())
				.failedAt(OffsetDateTime.now())
				.build();
			eventPublisher.publishPaymentFailed(failureEvent);
			log.error("결제 처리 중 오류 발생: transactionId={}, error={}", transactionId, e.getMessage(), e);
			throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("결제 확인 요청 종료: transactionId={}, 처리 시간={}ms", transactionId, (endTime - startTime));
			MDC.remove(TRANSACTION_ID);
		}
	}

	@Override
	public Payment.Response getPaymentStatus(String orderId, String paymentKey) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put(TRANSACTION_ID, transactionId);
		log.info("결제 상태 조회 요청 시작: transactionId={}, orderId={}, paymentKey={}", transactionId, orderId, paymentKey);
		long startTime = System.currentTimeMillis();

		try {
			String cacheKey = generateCacheKey(orderId, paymentKey);
			Payment.Response cachedPayment = getCachedPayment(cacheKey);

			if (cachedPayment != null) {
				log.info("Redis 캐시에서 결제 정보 조회 성공: transactionId={}, cachedPayment={}", transactionId, cachedPayment);
				return cachedPayment;
			}

			log.info("Redis 캐시에 결제 정보가 없음. 외부 API 조회 시작: transactionId={}", transactionId);

			Payment.Response paymentResponse = performPaymentStatusCheck(orderId, paymentKey);

			cachePaymentResponse(cacheKey, paymentResponse);
			log.info("결제 정보 Redis 캐시에 저장 완료: transactionId={}, paymentResponse={}", transactionId, paymentResponse);

			return paymentResponse;
		} catch (Exception e) {
			log.error("결제 상태 조회 중 오류 발생: transactionId={}, error={}", transactionId, e.getMessage(), e);
			throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "결제 상태 조회 중 오류가 발생했습니다: " + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("결제 상태 조회 요청 종료: transactionId={}, 처리 시간={}ms", transactionId, (endTime - startTime));
			MDC.remove(TRANSACTION_ID);
		}
	}

	private void validatePaymentInfo(Payment.Request request) {
		log.debug("결제 정보 유효성 검사 시작: {}", request);

		if (request.getPaymentKey() == null || request.getPaymentKey().isEmpty()) {
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_KEY, "결제 키가 필요합니다");
		}
		if (request.getOrderId() == null || request.getOrderId().isEmpty()) {
			throw new PaymentException(ErrorCode.INVALID_ORDER_ID, "주문 ID가 필요합니다");
		}
		if (request.getAmount() == null || request.getAmount() <= 0) {
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_AMOUNT, "유효하지 않은 결제 금액입니다");
		}

		log.debug("결제 정보 유효성 검사 완료");
	}

	private void checkExistingPayment(String orderId) {
		log.debug("기존 결제 확인 시작. 주문 ID: {}", orderId);

		Optional<PaymentEntity> existingPayment = paymentRepository.findByOrderId(orderId);
		if (existingPayment.isPresent()) {
			throw new PaymentException(ErrorCode.DUPLICATE_PAYMENT, "이 주문에 대한 결제가 이미 존재합니다");
		}

		log.debug("기존 결제 확인 완료. 중복 결제 없음");
	}

	private Payment.Response callTossPaymentApi(Payment.Request request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(tossPaymentConfig.getTestSecreteApiKey(), "");
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Payment.Request> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Payment.Response> response = restTemplate.postForEntity(
			TossPaymentConfig.URL + request.getPaymentKey(),
			entity,
			Payment.Response.class
		);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 확인에 실패했습니다");
		}

		return response.getBody();
	}

	private void savePaymentInfo(Payment.Response response) {
		log.debug("결제 정보 저장 시작: {}", response);
		PaymentEntity payment = PaymentEntity.builder()
			.orderId(response.getOrderId())
			.paymentKey(response.getPaymentKey())
			.totalAmount(Long.valueOf(response.getTotalAmount()))
			.status(response.getStatus())
			.paymentType("TOSS")
			.approvedAt(response.getApprovedAt() != null ? response.getApprovedAt().toLocalDateTime() : null)
			.build();

		paymentRepository.save(payment);
		log.debug("결제 정보 저장 완료");
	}

	private String generateCacheKey(String orderId, String paymentKey) {
		if (paymentKey != null && !paymentKey.isEmpty()) {
			return PAYMENT_CACHE_KEY_PREFIX + "paymentKey:" + paymentKey;
		} else if (orderId != null && !orderId.isEmpty()) {
			return PAYMENT_CACHE_KEY_PREFIX + "orderId:" + orderId;
		} else {
			throw new PaymentException(ErrorCode.INVALID_REQUEST, "결제 키 또는 주문 ID 중 하나는 반드시 제공되어야 합니다");
		}
	}

	private Payment.Response getCachedPayment(String cacheKey) {
		try {
			Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
			if (cachedObject instanceof Payment.Response response) {
				return response;
			}
		} catch (Exception e) {
			log.warn("Redis 캐시 조회 중 오류 발생: {}", e.getMessage());
		}
		return null;
	}

	private Payment.Response performPaymentStatusCheck(String orderId, String paymentKey) {
		log.debug("외부 API를 통한 결제 상태 조회 시작: orderId={}, paymentKey={}", orderId, paymentKey);
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(tossPaymentConfig.getTestSecreteApiKey(), "");
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<?> entity = new HttpEntity<>(headers);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TossPaymentConfig.URL)
			.pathSegment(paymentKey != null ? paymentKey : "orders", paymentKey != null ? "" : orderId);
		String url = builder.toUriString();

		ResponseEntity<String> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			entity,
			String.class
		);

		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				return objectMapper.readValue(response.getBody(), Payment.Response.class);
			} catch (Exception e) {
				log.error("결제 상태 응답 파싱 실패: {}", e.getMessage(), e);
				throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 상태 응답 파싱에 실패했습니다");
			}
		} else {
			log.error("결제 상태 조회 실패: statusCode={}", response.getStatusCode());
			throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "결제 상태 조회에 실패했습니다");
		}
	}

	private void cachePaymentResponse(String cacheKey, Payment.Response paymentResponse) {
		try {
			redisTemplate.opsForValue().set(cacheKey, paymentResponse, Duration.ofSeconds(60));
		} catch (RedisConnectionFailureException e) {
			log.error("Redis 연결 실패로 캐시 저장 실패: {}", e.getMessage(), e);
		}
	}

	private void invalidateCache(String orderId, String paymentKey) {
		String cacheKey = generateCacheKey(orderId, paymentKey);
		try {
			Boolean result = redisTemplate.delete(cacheKey);
			log.debug("Redis 캐시 무효화: {} (성공 여부: {})", cacheKey, result);
		} catch (RedisConnectionFailureException e) {
			log.error("Redis 연결 실패 중 캐시 무효화 시도: {}", e.getMessage(), e);
		}
	}
}
