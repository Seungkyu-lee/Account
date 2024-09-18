package com.example.pay.service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.pay.config.TossPaymentConfig;
import com.example.pay.domain.PaymentEntity;
import com.example.pay.dto.Payment;
import com.example.pay.exception.PaymentException;
import com.example.pay.repository.PaymentRepository;
import com.example.pay.type.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final TossPaymentConfig tossPaymentConfig;
	private final RestTemplate restTemplate;
	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public Payment.Response confirmPayment(Payment.Request request) {
		log.info("결제 확인 요청: {}", request);
		try {

			validatePaymentInfo(request);
			checkExistingPayment(request.getOrderId());

			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(tossPaymentConfig.getTestSecreteApiKey(), "");
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Payment.Request> entity = new HttpEntity<>(request, headers);

			ResponseEntity<Payment.Response> response = restTemplate.exchange(
				TossPaymentConfig.URL + request.getPaymentKey(),
				HttpMethod.POST,
				entity,
				Payment.Response.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				log.info("결제 확인 성공: {}", response.getBody());
				savePaymentInfo(Objects.requireNonNull(response.getBody()));
				return response.getBody();
			} else {
				log.error("결제 확인 실패: {}", response.getStatusCode());
				throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 확인에 실패했습니다");
			}
		} catch (Exception e) {
			log.error("결제 처리 중 오류 발생: {}", e.getMessage());
			throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@Override
	public Payment.Response getPaymentStatus(String orderId, String paymentKey) {
		log.info("결제 상태 조회 요청: orderId={}, paymentKey={}", orderId, paymentKey);

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(tossPaymentConfig.getTestSecreteApiKey(), "");
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url;
			if (paymentKey != null) {
				url = TossPaymentConfig.URL + paymentKey;
			} else if (orderId != null) {
				url = TossPaymentConfig.URL + "orders/" + orderId;
			} else {
				throw new PaymentException(ErrorCode.INVALID_REQUEST, "결제 키 또는 주문 ID 중 하나는 반드시 제공되어야 합니다");
			}

			ResponseEntity<Payment.Response> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				Payment.Response.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				log.info("결제 상태 조회 성공: {}", response.getBody());
				return response.getBody();
			} else {
				log.error("결제 상태 조회 실패: {}", response.getStatusCode());
				throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "결제 상태 조회에 실패했습니다");
			}
		} catch (Exception e) {
			log.error("결제 상태 조회 중 오류 발생: {}", e.getMessage());
			throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "결제 상태 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	private void validatePaymentInfo(Payment.Request request) {
		log.info("결제 정보 유효성 검사 시작: {}", request);

		if (request.getPaymentKey() == null || request.getPaymentKey().isEmpty()) {
			log.error("유효하지 않은 결제 키: {}", request.getPaymentKey());
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_KEY, "결제 키가 필요합니다");
		}
		if (request.getOrderId() == null || request.getOrderId().isEmpty()) {
			log.error("유효하지 않은 주문 ID: {}", request.getOrderId());
			throw new PaymentException(ErrorCode.INVALID_ORDER_ID, "주문 ID가 필요합니다");
		}
		if (request.getAmount() == null || request.getAmount() <= 0) {
			log.error("유효하지 않은 결제 금액: {}", request.getAmount());
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_AMOUNT, "유효하지 않은 결제 금액입니다");
		}

		log.info("결제 정보 유효성 검사 완료");
	}

	private void checkExistingPayment(String orderId) {
		log.info("기존 결제 확인 시작. 주문 ID: {}", orderId);

		Optional<PaymentEntity> existingPayment = paymentRepository.findByOrderId(orderId);
		if (existingPayment.isPresent()) {
			log.error("중복된 결제 발견. 주문 ID: {}", orderId);
			throw new PaymentException(ErrorCode.DUPLICATE_PAYMENT, "이 주문에 대한 결제가 이미 존재합니다");
		}

		log.info("기존 결제 확인 완료. 중복 결제 없음");
	}

	private void savePaymentInfo(Payment.Response response) {
		PaymentEntity payment = PaymentEntity.builder()
			.orderId(response.getOrderId())
			.paymentKey(response.getPaymentKey())
			.totalAmount(Long.valueOf(response.getTotalAmount()))
			.status(response.getStatus())
			.paymentType("TOSS")
			.approvedAt(response.getApprovedAt().toLocalDateTime())
			.build();

		paymentRepository.save(payment);
	}
}
