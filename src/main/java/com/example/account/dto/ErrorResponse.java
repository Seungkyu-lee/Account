package com.example.account.dto;

import com.example.account.type.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private final ErrorCode errorCode;
	private final String errorMessage;
}
