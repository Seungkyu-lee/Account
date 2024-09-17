package com.example.pay.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pay.domain.PaymentEntity;
import com.example.pay.dto.Payment;
import com.example.pay.dto.PaymentDto;
import com.example.pay.exception.PaymentException;
import com.example.pay.repository.PaymentRepository;
import com.example.pay.type.ErrorCode;
import com.example.pay.type.TransactionResultType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public Payment.Response paying(BigDecimal amount, String orderId, String paymentKey) {
		try {
			log.info("결제 처리 시작: orderId={}, amount={}, paymentKey={}", orderId, amount, paymentKey);
			validatePaymentInfo(amount, orderId, paymentKey);

			PaymentEntity existingPayment = paymentRepository.findByOrderId(orderId).orElse(null);
			if (existingPayment != null) {
				log.warn("이미 처리된 결제: orderId={}", orderId);
				throw new PaymentException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
			}
			long amountToBigDecimal = amount.movePointRight(2).longValueExact();

			PaymentEntity paymentEntity = PaymentEntity.builder()
				.transactionResultType(TransactionResultType.SUCCESS)
				.orderId(orderId)
				.totalAmount(amountToBigDecimal)
				.paymentKey(paymentKey)
				.requestedAt(LocalDateTime.now())
				.build();

			PaymentEntity savedPayment = paymentRepository.save(paymentEntity);
			PaymentDto paymentDto = PaymentDto.fromEntity(savedPayment);

			log.info("결제 처리 완료: orderId={}", orderId);
			return Payment.Response.from(paymentDto);
		} catch (PaymentException e) {
			log.error("결제 처리 중 예외 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("결제 처리 중 예기치 않은 오류 발생", e);
			throw new PaymentException(ErrorCode.PAYMENT_PROCESSING_ERROR, "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@Override
	public Payment.StatusResponse getPaymentStatus(String orderId, String paymentKey) {
		log.info("결제 상태 조회: orderId={}, paymentKey={}", orderId, paymentKey);
		PaymentEntity payment;
		if (orderId != null && !orderId.trim().isEmpty()) {
			payment = paymentRepository.findByOrderId(orderId)
				.orElseThrow(() -> {
					log.warn("주문 ID에 해당하는 결제를 찾을 수 없음: orderId={}", orderId);
					return new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "주문 ID에 해당하는 결제를 찾을 수 없습니다: " + orderId);
				});
		} else if (paymentKey != null && !paymentKey.trim().isEmpty()) {
			payment = paymentRepository.findByPaymentKey(paymentKey)
				.orElseThrow(() -> {
					log.warn("결제 키에 해당하는 결제를 찾을 수 없음: paymentKey={}", paymentKey);
					return new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, "결제 키에 해당하는 결제를 찾을 수 없습니다: " + paymentKey);
				});
		} else {
			log.warn("잘못된 요청: 주문 ID 또는 결제 키가 제공되지 않음");
			throw new PaymentException(ErrorCode.INVALID_REQUEST, "주문 ID 또는 결제 키 중 하나는 반드시 제공되어야 합니다.");
		}

		PaymentDto paymentDto = PaymentDto.fromEntity(payment);
		log.info("결제 상태 조회 완료: orderId={}, status={}", payment.getOrderId(), payment.getTransactionResultType());
		return Payment.StatusResponse.from(paymentDto);
	}

	private void validatePaymentInfo(BigDecimal amount, String orderId, String paymentKey) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			log.warn("유효하지 않은 결제 금액: amount={}", amount);
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_AMOUNT);
		}
		if (orderId == null || orderId.trim().isEmpty()) {
			log.warn("유효하지 않은 주문 ID");
			throw new PaymentException(ErrorCode.INVALID_ORDER_ID);
		}
		if (paymentKey == null || paymentKey.trim().isEmpty()) {
			log.warn("유효하지 않은 결제 키");
			throw new PaymentException(ErrorCode.INVALID_PAYMENT_KEY);
		}
	}
}
