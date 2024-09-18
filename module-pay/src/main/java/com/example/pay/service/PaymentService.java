package com.example.pay.service;

import com.example.pay.dto.Payment;

public interface PaymentService {
	Payment.Response confirmPayment(Payment.Request request);

	Payment.Response getPaymentStatus(String orderId, String paymentKey);
}
