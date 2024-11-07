package com.example.pay.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.pay.domain.PaymentStatusChangeEvent;
import com.example.pay.dto.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {
	private final RedisTemplate<String, Object> redisTemplate;
	private final ApplicationEventPublisher eventPublisher;

	public void publishPaymentStatusChange(String orderId, String status) {
		PaymentStatusChangeEvent event = new PaymentStatusChangeEvent(orderId, status);
		log.info("결제 상태 변경 이벤트 발행: orderId={}, status={}", orderId, status);
		redisTemplate.convertAndSend("payment-status-channel", event);
	}

	public void publishPaymentCompleted(Payment.PaymentEventDto event) {
		log.info("결제 완료 이벤트 발행: orderId={}, status={}, amount={}",
			event.getOrderId(), event.getStatus(), event.getAmount());
		eventPublisher.publishEvent(event);
	}

	public void publishPaymentFailed(Payment.PaymentFailureEvent event) {
		log.info("결제 실패 이벤트 발행: orderId={}, errorCode={}",
			event.getOrderId(), event.getErrorCode());
		eventPublisher.publishEvent(event);
	}
}
