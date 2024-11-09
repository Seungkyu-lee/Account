package com.example.pay.exception;

import com.example.pay.type.ErrorCode;

import lombok.Getter;

@Getter
public class BatchJobException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String errorMessage;

	public BatchJobException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}

	public BatchJobException(ErrorCode errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
