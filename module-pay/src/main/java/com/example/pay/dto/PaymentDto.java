package com.example.pay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.pay.domain.PaymentEntity;
import com.example.pay.type.TransactionResultType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
	private String paymentKey;
	private String orderId;
	private BigDecimal totalAmount;
	private LocalDateTime requestedAt;
	private TransactionResultType transactionResultType;

	public static PaymentDto fromEntity(PaymentEntity entity) {
		return PaymentDto.builder()
			.paymentKey(entity.getPaymentKey())
			.orderId(entity.getOrderId())
			.totalAmount(BigDecimal.valueOf(entity.getTotalAmount()))
			.requestedAt(entity.getRequestedAt())
			.transactionResultType(entity.getTransactionResultType())
			.build();
	}
}
