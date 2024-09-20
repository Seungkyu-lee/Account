package com.example.pay.controller;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public CommonResponse<Payment.Response> processPayment(@RequestBody Payment.Request request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);
		long startTime = System.currentTimeMillis();

		log.info("결제 처리 시작: transactionId={}, orderId={}, amount={}, paymentKey={}",
			transactionId, request.getOrderId(), request.getAmount(), request.getPaymentKey());

		try {
			Payment.Response response = paymentService.confirmPayment(request);
			log.info("결제 처리 완료: transactionId={}, status={}, responseTime={}ms",
				transactionId, response.getStatus(), System.currentTimeMillis() - startTime);
			return CommonResponse.success(response);
		} catch (Exception e) {
			log.error("결제 처리 중 오류 발생: transactionId={}, error={}", transactionId, e.getMessage(), e);
			throw e;
		} finally {
			MDC.clear();
		}
	}

	@GetMapping("/status")
	public CommonResponse<Payment.Response> getPaymentStatus(
		@RequestParam(required = false) String orderId,
		@RequestParam(required = false) String paymentKey) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);
		long startTime = System.currentTimeMillis();

		log.info("결제 상태 조회 시작: transactionId={}, orderId={}, paymentKey={}",
			transactionId, orderId, paymentKey);

		try {
			if ((orderId == null || orderId.isEmpty()) && (paymentKey == null || paymentKey.isEmpty())) {
				throw new IllegalArgumentException("orderId 또는 paymentKey 중 하나는 반드시 제공되어야 합니다.");
			}

			Payment.Response status = paymentService.getPaymentStatus(orderId, paymentKey);
			log.info("결제 상태 조회 완료: transactionId={}, status={}, responseTime={}ms",
				transactionId, status.getStatus(), System.currentTimeMillis() - startTime);
			return CommonResponse.success(status);
		} catch (Exception e) {
			log.error("결제 상태 조회 중 오류 발생: transactionId={}, error={}", transactionId, e.getMessage(), e);
			throw e;
		} finally {
			MDC.clear();
		}
	}
}
