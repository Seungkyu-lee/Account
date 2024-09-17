package com.example.pay.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class RandomNumberService {

	private static final SecureRandom random = new SecureRandom();

	public String generateRandomNumber() {
		int length = random.nextInt(59) + 6; // 6에서 64 사이의 랜덤한 길이
		StringBuilder sb = new StringBuilder(length);

		// 첫 번째 숫자가 0이 아니도록 함
		sb.append(random.nextInt(9) + 1);

		// 나머지 숫자 생성
		for (int i = 1; i < length; i++) {
			sb.append(random.nextInt(10));
		}

		return sb.toString();
	}
}
