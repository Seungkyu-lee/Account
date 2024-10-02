package com.example.pay.security;

import java.io.IOException;
import java.util.List;
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
	private final List<String> restrictedUrls;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestId = UUID.randomUUID().toString();
		MDC.put("requestId", requestId);

		try {
			String requestUri = request.getRequestURI();

			// URL 제한 검사
			if (isRestrictedUrl(requestUri)) {
				log.warn("접근이 제한된 URL입니다: {}", requestUri);
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to this URL is restricted");
				return;
			}

			String token = this.resolveTokenFromRequest(request);

			if (token != null && tokenProvider.validateToken(token)) {
				Authentication auth = tokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(auth);

				String username = tokenProvider.getUsername(token);
				MDC.put("username", username);

				log.info("인증된 사용자 {}가 {}에 접근합니다", username, requestUri);
			} else {
				log.info("유효한 JWT 토큰을 찾을 수 없습니다, uri: {}", requestUri);
			}

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private boolean isRestrictedUrl(String requestUri) {
		return restrictedUrls.stream().anyMatch(requestUri::contains);
	}

	private String resolveTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader(TOKEN_HEADER);

		if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
			return token.substring(TOKEN_PREFIX.length());
		}

		return null;
	}
}
