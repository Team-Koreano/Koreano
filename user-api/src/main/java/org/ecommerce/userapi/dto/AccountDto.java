package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.Users;

import jakarta.validation.constraints.NotEmpty;
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
			@NotEmpty(message = "계좌번호를 입력해주세요")
			String number,
			@NotEmpty(message = "은행명을 입력해주세요")
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
			public static Register of(final AccountDto accountDto){
				return new Register(
					accountDto.id,
					accountDto.number,
					accountDto.bankName
				);
			}
		}
	}
}
