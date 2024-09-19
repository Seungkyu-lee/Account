package com.example.pay.controller;

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

	@PostMapping("/signup")
	public CommonResponse<?> signup(@RequestBody Auth.SignUp request) {
		var result = this.memberService.register(request);
		return CommonResponse.success(result);
	}

	@PostMapping("/login")
	public CommonResponse<String> login(@RequestBody Auth.SignIn request, HttpServletRequest httpRequest) {
		// 사용자 인증
		MemberEntity member = memberService.authenticate(request);

		// 요청 헤더에서 "Auth-Type" 헤더를 확인하여 JWT 또는 세션 인증을 선택
		String authType = httpRequest.getHeader("Auth-Type");

		if ("JWT".equalsIgnoreCase(authType)) {
			// JWT 인증 방식
			String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());

			// 성공 응답: 토큰을 데이터로 포함
			return CommonResponse.success(token);
		} else {
			// 세션 기반 인증 방식
			HttpSession session = httpRequest.getSession(true);
			session.setAttribute("user", member);

			// 성공 응답: 메시지를 데이터로 포함하거나, 필요한 데이터를 포함
			return CommonResponse.success("로그인에 성공했습니다.");
		}
	}
}
