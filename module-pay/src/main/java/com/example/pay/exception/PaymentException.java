package com.example.pay.exception;

import com.example.pay.type.ErrorCode;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String errorMessage;

	public PaymentException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}

	public PaymentException(ErrorCode errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
