package com.example.pay.service;

import java.util.Objects;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.pay.domain.PaymentStatusChangeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusSubscriber implements MessageListener {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			PaymentStatusChangeEvent event = (PaymentStatusChangeEvent)redisTemplate
				.getValueSerializer().deserialize(message.getBody());
			log.info("결제 상태 변경 이벤트 수신: orderId={}, status={}, timestamp={}",
				Objects.requireNonNull(event).getOrderId(), event.getStatus(), event.getTimestamp());
			processPaymentStatusChange(event);
		} catch (Exception e) {
			log.error("결제 상태 변경 이벤트 처리 중 오류 발생", e);
		}
	}

	private void processPaymentStatusChange(PaymentStatusChangeEvent event) {
		switch (event.getStatus()) {
			case "DONE":
				log.info("결제 완료 처리: orderId={}", event.getOrderId());
				// 결제 완료에 따른 로직 (예: 주문 상태 업데이트, 이메일 발송 등)
				break;
			case "CANCELED":
				log.info("결제 취소 처리: orderId={}", event.getOrderId());
				// 결제 취소에 따른 로직
				break;
			case "FAILED":
				log.info("결제 실패 처리: orderId={}", event.getOrderId());
				// 결제 실패에 따른 로직
				break;
			default:
				log.warn("처리되지 않은 결제 상태: orderId={}, status={}", event.getOrderId(), event.getStatus());
		}
	}
}
