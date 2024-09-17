package com.example.pay.exception;

import com.example.pay.type.ErrorCode;

import lombok.Getter;

@Getter
public class AccountException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String errorMessage;

	public AccountException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}

	public AccountException(ErrorCode errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
