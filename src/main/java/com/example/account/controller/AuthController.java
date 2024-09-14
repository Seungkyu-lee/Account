package com.example.account.controller;

import com.example.account.domain.Auth;
import com.example.account.dto.CommonResponse;
import com.example.account.security.TokenProvider;
import com.example.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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