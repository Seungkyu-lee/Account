package com.example.pay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pay.config.TossPaymentConfig;
import com.example.pay.dto.CommonResponse;
import com.example.pay.dto.Payment;
import com.example.pay.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

	private final TossPaymentConfig tossPaymentConfig;
	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public CommonResponse<Payment.Response> processPayment(@RequestBody Payment.Request request) {
		log.info("결제 처리 요청: orderId={}, amount={}, paymentKey={}",
			request.getOrderId(), request.getAmount(), request.getPaymentKey());
		Payment.Response response = paymentService.confirmPayment(request);
		return CommonResponse.success(response);
	}

	@GetMapping("/status")
	public CommonResponse<Payment.Response> getPaymentStatus(
		@RequestParam(required = false) String orderId,
		@RequestParam(required = false) String paymentKey) {
		log.info("결제 상태 조회 요청: orderId={}, paymentKey={}", orderId, paymentKey);
		Payment.Response status = paymentService.getPaymentStatus(orderId, paymentKey);
		log.info("결제 상태 조회 완료: status={}", status);
		return CommonResponse.success(status);
	}
}
