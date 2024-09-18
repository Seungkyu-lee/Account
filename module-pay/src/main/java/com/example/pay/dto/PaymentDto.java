package com.example.pay.dto;

import java.time.LocalDateTime;

import com.example.pay.domain.PaymentEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

	private Long id;
	private String orderId;
	private String paymentKey;
	private Long totalAmount;
	private String status;
	private String paymentType;
	private LocalDateTime approvedAt;

	// Entity를 DTO로 변환하는 정적 메서드
	public static PaymentDto fromEntity(PaymentEntity entity) {
		return PaymentDto.builder()
			.id(entity.getId())
			.orderId(entity.getOrderId())
			.paymentKey(entity.getPaymentKey())
			.totalAmount(entity.getTotalAmount())
			.status(entity.getStatus())
			.paymentType(entity.getPaymentType())
			.approvedAt(entity.getApprovedAt())
			.build();
	}

	// DTO를 Entity로 변환하는 메서드
	public PaymentEntity toEntity() {
		return PaymentEntity.builder()
			.orderId(this.orderId)
			.paymentKey(this.paymentKey)
			.totalAmount(this.totalAmount)
			.status(this.status)
			.paymentType(this.paymentType)
			.approvedAt(this.approvedAt)
			.build();
	}
}
