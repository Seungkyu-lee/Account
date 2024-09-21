package com.example.pay.security;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestId = UUID.randomUUID().toString();
		MDC.put("requestId", requestId);

		try {
			String token = this.resolveTokenFromRequest(request);

			if (token != null && tokenProvider.validateToken(token)) {
				Authentication auth = tokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(auth);

				String username = tokenProvider.getUsername(token);
				MDC.put("username", username);

				log.info("인증된 사용자 {}가 {}에 접근합니다", username, request.getRequestURI());
			} else {
				log.info("유효한 JWT 토큰을 찾을 수 없습니다, uri: {}", request.getRequestURI());
			}

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String resolveTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader(TOKEN_HEADER);

		if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
			return token.substring(TOKEN_PREFIX.length());
		}

		return null;
	}
}
