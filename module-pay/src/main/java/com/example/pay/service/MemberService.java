package com.example.pay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import com.example.pay.domain.Auth;
import com.example.pay.domain.MemberEntity;
import com.example.pay.exception.AccountException;
import com.example.pay.repository.MemberRepository;
import com.example.pay.type.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public MemberEntity register(Auth.SignUp member) {
		boolean exists = memberRepository.existsByUsername(member.getUsername());
		if (exists) {
			throw new AccountException(ErrorCode.ACCOUNT_IS_ALREADY);
		}

		// 패스워드 인코딩
		member.setPassword(passwordEncoder.encode(member.getPassword()));

		MemberEntity savedMember = memberRepository.save(member.toEntity());
		// 비동기로 이메일 발송
		log.info("register: " + Thread.currentThread().getName());
		emailService.sendRegistrationEmail(savedMember.getEmail(), savedMember.getUsername());

		return savedMember;
	}

	public MemberEntity authenticate(Auth.SignIn member) {
		MemberEntity user = memberRepository.findByUsername(member.getUsername())
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

		// 비밀번호 검증
		if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
			throw new AccountException(ErrorCode.PASSWORD_NOT_MATCHED);
		}

		return user;
	}

	public MemberEntity findByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
	}
}

