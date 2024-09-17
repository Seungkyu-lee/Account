package com.example.pay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pay.domain.Auth;
import com.example.pay.dto.CommonResponse;
import com.example.pay.security.TokenProvider;
import com.example.pay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;

	@PostMapping("/signup")
	public CommonResponse<?> signup(@RequestBody Auth.SignUp request) {
		var result = this.memberService.register(request);
		return CommonResponse.success(result);
	}

	@PostMapping("/signin")
	public CommonResponse<String> signin(@RequestBody Auth.SignIn request) {
		var member = this.memberService.authenticate(request);
		var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
		log.info("user login -> " + request.getUsername());
		return CommonResponse.success(token);
	}
}
