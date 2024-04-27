package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.UserStatus;
import org.ecommerce.userapi.exception.UserErrorMessages;
import org.ecommerce.userapi.security.JwtUtils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerDto {
	private Integer id;
	private String email;
	private String name;
	private String password;
	private String address;
	private String phoneNumber;
	private LocalDateTime createDatetime;
	private boolean isDeleted;
	private LocalDateTime updateDatetime;
	private Long beanPayId;
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
			@NotBlank(message = UserErrorMessages.addressNotBlank)
			String address,
			@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
			String phoneNumber
		) {
		}

		public record Login(
			@Email
			@NotBlank(message = UserErrorMessages.emailNotBlank)
			String email,
			@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
			String password
		) {
		}
	}

	public static class Response {

		public record Register(
			String email,
			String name,
			String address,
			String phoneNumber
		) {
			public static Register of(final SellerDto seller) {
				return new Register(
					seller.getEmail(),
					seller.getName(),
					seller.getAddress(),
					seller.getPhoneNumber()
				);
			}

		}

		public record Login(
			String accessToken
		) {
			public static Login of(final SellerDto sellerDto) {
				return new Login(JwtUtils.prefix(sellerDto.getAccessToken()));
			}
		}
	}
}
