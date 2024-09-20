package com.example.pay.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);
		request.setAttribute("startTime", System.currentTimeMillis());
		log.info("Request started: {} {}", request.getMethod(), request.getRequestURI());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) {
		long duration = System.currentTimeMillis() - (Long)request.getAttribute("startTime");
		log.info("Request completed: {} {} ({}ms)", request.getMethod(), request.getRequestURI(), duration);
		MDC.clear();
	}
}
