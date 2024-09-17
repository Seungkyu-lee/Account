package com.example.pay.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pay.domain.Auth;
import com.example.pay.domain.MemberEntity;
import com.example.pay.exception.AccountException;
import com.example.pay.repository.MemberRepository;
import com.example.pay.type.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.memberRepository.findByUsername(username)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_IS_ALREADY));
	}

	public MemberEntity register(Auth.SignUp member) {
		boolean exists = this.memberRepository.existsByUsername(member.getUsername());
		if (exists) {
			throw new AccountException(ErrorCode.ACCOUNT_IS_ALREADY);
		}

		member.setPassword(this.passwordEncoder.encode(member.getPassword()));
		return this.memberRepository.save(member.toEntity());
	}

	public MemberEntity authenticate(Auth.SignIn member) {
		var user = this.memberRepository.findByUsername(member.getUsername())
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

		if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
			throw new AccountException(ErrorCode.PASSWORD_NOT_MATCHED);
		}

		return user;
	}
}
