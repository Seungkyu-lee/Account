package com.example.pay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PayApplication.class)
class PayApplicationTests {

	@Test
	void contextLoads() {
		/*
		 * 이 메서드는 의도적으로 비어 있습니다.
		 * 이 테스트의 목적은 Spring 애플리케이션 컨텍스트가 성공적으로 로드되는지 확인하는 것입니다.
		 * 빈 구성이나 애플리케이션 설정에 문제가 있다면,
		 * 이 테스트는 컨텍스트 초기화 과정에서 실패할 것입니다.
		 * 테스트의 성공은 애플리케이션 컨텍스트가 오류 없이 로드되었음을 의미하므로,
		 * 추가적인 assertion이 필요하지 않습니다.
		 */
	}

}
