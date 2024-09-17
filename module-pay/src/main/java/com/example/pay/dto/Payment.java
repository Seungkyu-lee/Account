package com.example.pay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.pay.type.TransactionResultType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Payment {
	@Getter
	@NoArgsConstructor(force = true)
	@AllArgsConstructor
	@Builder
	@Valid
	public static class Request {
		@NotNull(message = "결제 금액은 필수입니다.")
		private final BigDecimal amount;

		@NotNull(message = "주문번호는 필수입니다.")
		@Size(min = 6, max = 64, message = "주문번호는 6자에서 64자 사이여야 합니다.")
		private final String orderId;

		@Size(max = 200, message = "paymentKey는 200자를 초과할 수 없습니다.")
		private final String paymentKey;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class Response {
		private final String paymentKey;
		private final String orderId;
		private final BigDecimal totalAmount;
		private final LocalDateTime requestedAt;

		public static Response from(PaymentDto dto) {
			return Response.builder()
				.paymentKey(dto.getPaymentKey())
				.orderId(dto.getOrderId())
				.totalAmount(dto.getTotalAmount())
				.requestedAt(dto.getRequestedAt())
				.build();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class StatusResponse {
		private final String paymentKey;
		private final String orderId;
		private final BigDecimal totalAmount;
		private final LocalDateTime requestedAt;
		private final TransactionResultType transactionResultType;

		public static StatusResponse from(PaymentDto dto) {
			return StatusResponse.builder()
				.orderId(dto.getOrderId())
				.totalAmount(dto.getTotalAmount())
				.transactionResultType(dto.getTransactionResultType())
				.requestedAt(dto.getRequestedAt())
				.paymentKey(dto.getPaymentKey())
				.build();
		}
	}
}
