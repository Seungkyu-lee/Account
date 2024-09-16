package com.example.pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.example.common.TestBean;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.pay", "com.example.common"})
public class PayApplication {
	// 의존성 확인을 위한 코드 - 시작
	private final TestBean testBean;

	@Autowired
	public PayApplication(TestBean testBean) {
		this.testBean = testBean;
	}

	@PostConstruct
	public void dependencyTest() {
		this.testBean.dependencyTest();
	}

	// 의존성 확인을 위한 코드 - 끝
	public static void main(String[] args) {
		SpringApplication.run(PayApplication.class, args);
	}

}
