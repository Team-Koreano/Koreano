package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.UserStatus;
import org.ecommerce.userapi.security.JwtUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {

	private Integer id;
	private String email;
	private String name;
	private String password;
	private Gender gender;
	private Short age;
	private String phoneNumber;
	private LocalDateTime createDatetime;
	private boolean isDeleted;
	private LocalDateTime updateDatetime;
	private Integer beanPay;
	private UserStatus userStatus;
	private String accessToken;

	public static class Request {
		public record Register(
			String email,
			String name,
			String password,
			Gender gender,
			Short age,
			String phoneNumber
		) {
		}

		public record Login(
			String email,
			String password
		) {
		}
	}

	public static class Response {
		public record Register(
			String email,
			String name,
			Gender gender,
			Short age,
			String phoneNumber
		) {
			public static Register of(final UserDto users) {
				return new Register(
					users.getEmail(),
					users.getName(),
					users.getGender(),
					users.getAge(),
					users.getPhoneNumber()
				);
			}
		}

		public record Login(
			String accessToken
		) {
			public static Login of(final UserDto userDto) {
				return new Login(JwtUtils.prefix(userDto.accessToken));
			}
		}
	}
}
