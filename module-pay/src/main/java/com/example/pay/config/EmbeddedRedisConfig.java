package com.example.pay.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() {
		redisServer = new RedisServer(6379); // 사용할 포트 번호 설정
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}
