package com.example.pay.controller;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppRunner implements ApplicationRunner {
	private final Environment environment;

	public AppRunner(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.debug("===================다중 프로파일 테스트===================");
		log.debug("Active profiles : {}", Arrays.toString(environment.getActiveProfiles()));
		log.debug("Datasource driver : {}", environment.getProperty("spring.datasource.driver-class-name"));
		log.debug("Datasource url : {}", environment.getProperty("spring.datasource.url"));
		log.debug("Datasource username : {}", environment.getProperty("spring.datasource.username"));
		log.debug("Datasource password : {}", environment.getProperty("spring.datasource.password"));
		log.debug("Server Port : {}", environment.getProperty("server.port"));
		log.debug("Default Property : {}", environment.getProperty("default.string"));
		log.debug("Common Property : {}", environment.getProperty("common.string"));
		log.debug("====================================================");
	}
}
