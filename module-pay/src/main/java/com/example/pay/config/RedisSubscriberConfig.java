package com.example.pay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.example.pay.service.PaymentStatusSubscriber;

@Configuration
public class RedisSubscriberConfig {
	@Bean
	MessageListenerAdapter messageListener(PaymentStatusSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber);
	}

	@Bean
	RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new ChannelTopic("payment-status-channel"));
		return container;
	}
}
