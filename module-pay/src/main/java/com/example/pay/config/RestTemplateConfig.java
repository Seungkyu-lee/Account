package com.example.pay.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {
		// ObjectMapper 설정
		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.findAndRegisterModules(); // 사용 가능한 모든 모듈 자동 등록

		// 커스터마이즈된 MappingJackson2HttpMessageConverter 생성
		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

		// 새로운 RestTemplate 인스턴스 생성
		RestTemplate restTemplate = new RestTemplate();

		// 기존의 MappingJackson2HttpMessageConverter 제거
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters().stream()
			.filter(conv -> !(conv instanceof MappingJackson2HttpMessageConverter))
			.collect(Collectors.toList());

		// 커스터마이즈된 Jackson 컨버터를 리스트의 처음에 추가
		converters.add(0, jacksonConverter);

		// 수정된 컨버터 리스트 설정
		restTemplate.setMessageConverters(converters);

		return restTemplate;
	}
}
