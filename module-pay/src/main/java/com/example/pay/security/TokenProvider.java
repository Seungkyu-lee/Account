package com.example.pay.security;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.pay.domain.MemberEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
	private static final String KEY_ROLES = "roles";

	private final UserDetailsService userDetailsService;

	@Value("${spring.jwt.secret}")
	private String secretKey;

	// JWT 토큰 생성
	public String generateToken(String username, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put(KEY_ROLES, roles);
		Date now = new Date();

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + TOKEN_EXPIRE_TIME))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	// JWT 토큰에서 인증 정보 조회
	public Authentication getAuthentication(String token) {
		MemberEntity userDetails = (MemberEntity)userDetailsService.loadUserByUsername(this.getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// JWT 토큰에서 사용자 이름 조회
	public String getUsername(String token) {
		return this.parseClaims(token).getSubject();
	}

	// JWT 토큰 유효성 검증
	public boolean validateToken(String token) {
		if (token == null) {
			return false;
		}

		try {
			Claims claims = this.parseClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	// Claims 파싱
	private Claims parseClaims(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody();
	}
}
