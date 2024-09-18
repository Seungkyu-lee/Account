package com.example.pay.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String orderId;

	@Column(nullable = false)
	private String paymentKey;

	@Column(nullable = false)
	private Long totalAmount;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private String paymentType;

	@Column(nullable = false)
	private LocalDateTime approvedAt;

}
