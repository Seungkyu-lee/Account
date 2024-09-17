package com.example.pay.dto;

import com.example.pay.type.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private final ErrorCode errorCode;
	private final String errorMessage;
}
