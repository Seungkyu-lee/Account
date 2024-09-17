package com.example.pay.service;

import java.math.BigDecimal;

import com.example.pay.dto.Payment;

public interface PaymentService {
	Payment.Response paying(BigDecimal amount, String orderId, String paymentKey);

	Payment.StatusResponse getPaymentStatus(String orderId, String paymentKey);
}
