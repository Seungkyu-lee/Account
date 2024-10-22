package com.example.pay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Async("taskExecutor")
	public void sendRegistrationEmail(String to, String username) {
		try {
			log.info("회원가입 이메일 발송 준비 중: {}", to);
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject("회원가입을 환영합니다");

			Context context = new Context();
			context.setVariable("username", username);
			String content = templateEngine.process("registrationEmail", context);

			helper.setText(content, true);

			mailSender.send(message);
			log.info("회원가입 이메일 발송 완료: {}", to);
		} catch (MessagingException e) {
			log.error("회원가입 이메일 발송 실패: {}. 오류: {}", to, e.getMessage());
		}
	}
}
