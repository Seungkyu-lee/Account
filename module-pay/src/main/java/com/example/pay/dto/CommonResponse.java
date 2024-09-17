package com.example.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
	private String resultCode;
	private String resultMessage;
	private T data;

	public static <T> CommonResponse<T> success(T data) {
		return new CommonResponse<>("SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
	}

	public static <T> CommonResponse<T> fail(String errorCode, String errorMessage) {
		return new CommonResponse<>(errorCode, errorMessage, null);
	}
}
