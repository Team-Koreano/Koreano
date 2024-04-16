package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.Users;

import jakarta.validation.constraints.NotEmpty;
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
			@NotEmpty(message = "주소지 별명을 입력해주세요")
			String name,
			@NotEmpty(message = "주소지를 입력해주세요")
			String postAddress,
			@NotEmpty(message = "상세 주소를 입력해주세요")
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
