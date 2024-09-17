package com.example.pay.config;

import org.springframework.context.annotation.Bean;

import com.example.pay.service.MemberService;

public class DomainConfig {
	@Bean
	MemberService userService() {
		// 생략
		return null;
	}
}
