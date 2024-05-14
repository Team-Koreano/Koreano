package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.ecommerce.userapi.exception.UserErrorMessages;
import org.ecommerce.userapi.security.JwtProvider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	private Integer beanPayId;
	private UserStatus userStatus;
	private String accessToken;

	public static class Request {
		public record Register(
			@NotBlank(message = UserErrorMessages.emailNotBlank)
			@Email
			String email,
			@NotBlank(message = UserErrorMessages.nameNotBlank)
			String name,
			@NotBlank(message = UserErrorMessages.passwordNotBlank)
			String password,
			Gender gender,
			@NotNull(message = UserErrorMessages.ageNotNull)
			Short age,
			@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
			String phoneNumber
		) {
		}

		public record Login(
			@Email
			@NotBlank(message = UserErrorMessages.emailNotBlank)
			String email,
			@NotBlank(message = UserErrorMessages.passwordNotBlank)
			String password
		) {
		}

		public record Withdrawal(
			@Email
			@NotBlank(message = UserErrorMessages.emailNotBlank)
			String email,
			@NotBlank(message = UserErrorMessages.passwordNotBlank)
			String password,
			@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
			String phoneNumber
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
		}

		public record Login(
			String accessToken
		) {
			public static Login of(final UserDto userDto) {
				return new Login(JwtProvider.prefix(userDto.accessToken));
			}
		}
	}
}
