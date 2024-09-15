package com.example.account.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.account.dto.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PaymentException.class)
	public CommonResponse<Void> handlePaymentException(PaymentException ex) {
		log.error("결제 예외 발생: {}", ex.getMessage());
		return CommonResponse.fail(ex.getErrorCode().name(), ex.getErrorMessage());
	}

	@ExceptionHandler(AccountException.class)
	public CommonResponse<Void> handleAccountException(AccountException exception) {
		log.error("계정 예외 발생: {}", exception.getMessage());
		return CommonResponse.fail(exception.getErrorCode().name(), exception.getErrorMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public CommonResponse<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
		log.error("잘못된 인자 예외 발생: {}", exception.getMessage());
		return CommonResponse.fail("INVALID_ARGUMENT", "잘못된 입력값입니다: " + exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public CommonResponse<Void> handleException(Exception exception) {
		log.error("예상치 못한 예외 발생", exception);
		return CommonResponse.fail("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해 주세요.");
	}
}
