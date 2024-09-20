package com.example.pay.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.pay.dto.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String TRANSACTION_ID = "transactionId";

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<CommonResponse<Void>> handlePaymentException(PaymentException ex) {
		String transactionId = MDC.get(TRANSACTION_ID);
		log.error("결제 예외 발생: transactionId={}, errorCode={}, message={}",
			transactionId, ex.getErrorCode(), ex.getMessage(), ex);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail(ex.getErrorCode().name(), ex.getErrorMessage()));
	}

	@ExceptionHandler(AccountException.class)
	public ResponseEntity<CommonResponse<Void>> handleAccountException(AccountException ex) {
		String transactionId = MDC.get(TRANSACTION_ID);
		log.error("계정 예외 발생: transactionId={}, errorCode={}, message={}",
			transactionId, ex.getErrorCode(), ex.getMessage(), ex);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail(ex.getErrorCode().name(), ex.getErrorMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
		String transactionId = MDC.get(TRANSACTION_ID);
		log.error("잘못된 인자 예외 발생: transactionId={}, message={}", transactionId, ex.getMessage(), ex);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail("INVALID_ARGUMENT", "잘못된 입력값입니다: " + ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
		String transactionId = MDC.get(TRANSACTION_ID);
		log.error("예상치 못한 예외 발생: transactionId={}", transactionId, ex);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(CommonResponse.fail("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해 주세요."));
	}
}
