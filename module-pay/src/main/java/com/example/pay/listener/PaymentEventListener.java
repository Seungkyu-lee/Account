package com.example.pay.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.pay.dto.Payment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentEventListener {

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePaymentCompleted(Payment.PaymentEventDto event) {
		log.info("결제 완료 이벤트 처리: orderId={}, status={}, amount={}",
			event.getOrderId(), event.getStatus(), event.getAmount());
		// 결제 완료 후처리 로직
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void handlePaymentFailed(Payment.PaymentFailureEvent event) {
		log.info("결제 실패 이벤트 처리: orderId={}, errorCode={}, errorMessage={}",
			event.getOrderId(), event.getErrorCode(), event.getErrorMessage());
		// 결제 실패 후처리 로직
	}
}
