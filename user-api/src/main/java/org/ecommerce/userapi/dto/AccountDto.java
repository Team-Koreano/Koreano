package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountDto {
	private Integer id;
	private Seller seller;
	private Users users;
	private String number;
	private String bankName;
	private LocalDateTime createDatetime;
	private boolean isDeleted;
	private LocalDateTime updateDatetime;

	public static class Request {
		public record Register(
			@NotBlank(message = UserErrorMessages.bankNumberNotBlank)
			String number,
			@NotBlank(message = UserErrorMessages.bankNameNotEmpty)
			String bankName
		) {
		}
	}

	public static class Response {
		public record Register(
			Integer id,
			String number,
			String bankName
		) {
		}
	}
}
