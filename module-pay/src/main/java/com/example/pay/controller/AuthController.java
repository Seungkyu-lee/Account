package com.example.pay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pay.domain.Auth;
import com.example.pay.domain.MemberEntity;
import com.example.pay.dto.CommonResponse;
import com.example.pay.security.TokenProvider;
import com.example.pay.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;
	private static final String AUTH_TYPE_HEADER = "Auth-Type";

	@PostMapping("/signup")
	public CommonResponse<MemberEntity> signup(@RequestBody Auth.SignUp request) {
		log.info("회원가입 요청 받음. 사용자명: {}", request.getUsername());
		var result = this.memberService.register(request);
		log.info("회원가입 성공. 사용자명: {}, 이메일: {}", result.getUsername(), result.getEmail());
		return CommonResponse.success(result);
	}

	@PostMapping("/login")
	public CommonResponse<String> login(@RequestBody Auth.SignIn request, HttpServletRequest httpRequest) {
		log.info("로그인 요청 받음. 사용자명: {}", request.getUsername());

		// 사용자 인증
		MemberEntity member = memberService.authenticate(request);

		// 요청 헤더에서 "Auth-Type" 헤더를 확인하여 JWT 또는 세션 인증을 선택
		String authType = httpRequest.getHeader(AUTH_TYPE_HEADER);

		if ("JWT".equalsIgnoreCase(authType)) {
			// JWT 인증 방식
			String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
			log.info("JWT 인증 성공. 사용자명: {}", member.getUsername());
			return CommonResponse.success(token);
		} else {
			// 세션 기반 인증 방식
			HttpSession session = httpRequest.getSession(true);
			session.setAttribute("user", member);
			log.info("세션 기반 인증 성공. 사용자명: {}", member.getUsername());
			return CommonResponse.success("로그인에 성공했습니다.");
		}
	}

	@PostMapping("/logout")
	public CommonResponse<String> logout(HttpServletRequest httpRequest) {
		String authType = httpRequest.getHeader(AUTH_TYPE_HEADER);

		if ("JWT".equalsIgnoreCase(authType)) {
			// JWT 로그아웃 처리 (클라이언트 측에서 토큰 삭제)
			log.info("JWT 로그아웃 요청 처리됨");
			return CommonResponse.success("JWT 로그아웃 성공");
		} else {
			// 세션 기반 로그아웃
			HttpSession session = httpRequest.getSession(false);
			if (session != null) {
				session.invalidate();
				log.info("세션 기반 로그아웃 성공");
			} else {
				log.info("로그아웃 요청 처리됨 (활성 세션 없음)");
			}
			return CommonResponse.success("로그아웃 성공");
		}
	}

	@GetMapping("/user")
	public CommonResponse<MemberEntity> getCurrentUser(HttpServletRequest httpRequest) {
		String authType = httpRequest.getHeader(AUTH_TYPE_HEADER);

		try {
			if ("JWT".equalsIgnoreCase(authType)) {
				// JWT에서 현재 사용자 정보 가져오기
				String token = tokenProvider.resolveToken(httpRequest);
				if (token != null && tokenProvider.validateToken(token)) {
					String username = tokenProvider.getUsername(token);
					MemberEntity member = memberService.findByUsername(username);
					log.info("JWT를 통해 현재 사용자 정보 조회. 사용자명: {}", username);
					return CommonResponse.success(member);
				} else {
					log.warn("유효한 JWT 토큰을 찾을 수 없음");
					return CommonResponse.fail("유효한 인증 토큰을 찾을 수 없습니다.", null);
				}
			} else {
				// 세션에서 현재 사용자 정보 가져오기
				HttpSession session = httpRequest.getSession(false);
				if (session != null && session.getAttribute("user") != null) {
					MemberEntity member = (MemberEntity)session.getAttribute("user");
					log.info("세션을 통해 현재 사용자 정보 조회. 사용자명: {}", member.getUsername());
					return CommonResponse.success(member);
				} else {
					log.warn("인증된 사용자 정보를 찾을 수 없음");
					return CommonResponse.fail("인증된 사용자 정보를 찾을 수 없습니다.", null);
				}
			}
		} catch (RuntimeException e) {
			log.error("사용자 정보 조회 중 오류 발생", e);
			return CommonResponse.fail("사용자 정보를 조회하는 중 오류가 발생했습니다.", null);
		}
	}

}
