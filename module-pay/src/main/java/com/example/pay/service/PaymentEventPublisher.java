package com.example.pay.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.pay.domain.PaymentStatusChangeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {
	private final RedisTemplate<String, Object> redisTemplate;

	public void publishPaymentStatusChange(String orderId, String status) {
		PaymentStatusChangeEvent event = new PaymentStatusChangeEvent(orderId, status);
		log.info("결제 상태 변경 이벤트 발행: orderId={}, status={}", orderId, status);
		redisTemplate.convertAndSend("payment-status-channel", event);
	}
}
