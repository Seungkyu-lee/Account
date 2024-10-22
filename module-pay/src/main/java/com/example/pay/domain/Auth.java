package com.example.pay.domain;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Auth {

	@Data
	public static class SignIn {
		private String username;
		private String password;
	}

	@Data
	public static class SignUp {
		private String username;
		private String password;
		private String email;
		private List<String> roles;

		public MemberEntity toEntity() {
			return MemberEntity.builder()
				.username(this.username)
				.password(this.password)
				.email(this.email)
				.roles(this.roles)
				.build();
		}
	}
}
