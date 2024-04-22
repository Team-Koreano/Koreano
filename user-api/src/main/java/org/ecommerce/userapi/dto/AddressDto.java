package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddressDto {
	private Integer id;
	private Users users;
	private String name;
	private String postAddress;
	private String detail;
	private LocalDateTime createDatetime;
	private boolean isDeleted;
	private LocalDateTime updateDatetime;

	public static class Request {
		public record Register(
			@NotBlank(message = UserErrorMessages.addressNameNotBlank)
			String name,
			@NotBlank(message = UserErrorMessages.postAddressNotBlank)
			String postAddress,
			@NotBlank(message = UserErrorMessages.addressDetailNotBlank)
			String detail
		) {
		}
	}

	public static class Response {
		public record Register(
			Integer id,
			String name,
			String postAddress,
			String detail
		) {
			public static Register of(final AddressDto addressDto) {
				return new Register(
					addressDto.id,
					addressDto.name,
					addressDto.postAddress,
					addressDto.detail
				);
			}
		}
	}
}
