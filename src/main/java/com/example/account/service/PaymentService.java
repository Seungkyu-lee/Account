package com.example.account.service;

import com.example.account.dto.Payment;
import java.math.BigDecimal;

public interface PaymentService {
    Payment.Response paying(BigDecimal amount, String orderId, String paymentKey);
    Payment.StatusResponse getPaymentStatus(String orderId, String paymentKey);
}