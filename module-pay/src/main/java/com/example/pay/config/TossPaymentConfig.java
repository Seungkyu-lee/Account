package com.example.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentConfig {
	public static final String URL = "https://api.tosspayments.com/v1/payments/";

	private String testSecreteApiKey;
	private String successUrl;
	private String failUrl;
}
