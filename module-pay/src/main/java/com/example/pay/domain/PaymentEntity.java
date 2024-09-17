package com.example.pay.domain;

import java.time.LocalDateTime;

import com.example.pay.type.TransactionResultType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class PaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String paymentKey;

	@Column(nullable = false, length = 64)
	private String orderId;

	@Column(nullable = false)
	private Long totalAmount;

	@Column(nullable = false)
	private LocalDateTime requestedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionResultType transactionResultType;

	private LocalDateTime approvedAt;

}
