package com.example.pay.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class InfrastructureConfig {

	// 자바 기반 설정 방식에서 메서드 레벨로 프로파일 정의
	@Bean(name = "dataSource")
	@Profile("dev")
	DataSource dataSourceForDevelopment() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.build();
	}

	@Bean(name = "dataSource")
	@Profile("test")
	DataSource dataSourceForStaging() {
		// 생략
		return null;
	}

	@Bean(name = "dataSource")
	@Profile("prod")
	DataSource dataSourceForProduction() {
		// 생략
		return null;
	}
}
