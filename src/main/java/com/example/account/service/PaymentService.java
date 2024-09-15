package com.example.account.service;

import java.math.BigDecimal;

import com.example.account.dto.Payment;

public interface PaymentService {
	Payment.Response paying(BigDecimal amount, String orderId, String paymentKey);

	Payment.StatusResponse getPaymentStatus(String orderId, String paymentKey);
}
