package com.example.pay.domain;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusChangeEvent implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String orderId;
	private String status;
	private long timestamp;

	public PaymentStatusChangeEvent(String orderId, String status) {
		this.orderId = orderId;
		this.status = status;
		this.timestamp = System.currentTimeMillis();
	}
}
