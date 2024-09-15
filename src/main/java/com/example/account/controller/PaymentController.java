package com.example.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.dto.CommonResponse;
import com.example.account.dto.Payment;
import com.example.account.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public CommonResponse<Payment.Response> processPayment(@RequestBody Payment.Request request) {
		log.info("결제 처리 요청: orderId={}, amount={}, paymentKey={}",
			request.getOrderId(), request.getAmount(), request.getPaymentKey());
		Payment.Response response = paymentService.paying(
			request.getAmount(),
			request.getOrderId(),
			request.getPaymentKey()
		);
		return CommonResponse.success(response);
	}

	@GetMapping("/status")
	public CommonResponse<Payment.StatusResponse> getPaymentStatus(
		@RequestParam(required = false) String orderId,
		@RequestParam(required = false) String paymentKey) {
		log.info("결제 상태 조회 요청: orderId={}, paymentKey={}", orderId, paymentKey);
		Payment.StatusResponse status = paymentService.getPaymentStatus(orderId, paymentKey);
		return CommonResponse.success(status);
	}
}
