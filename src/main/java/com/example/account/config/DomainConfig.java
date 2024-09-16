package com.example.account.config;

import org.springframework.context.annotation.Bean;

import com.example.account.service.MemberService;

public class DomainConfig {
	@Bean
	MemberService userService() {
		// 생략
		return null;
	}
}
