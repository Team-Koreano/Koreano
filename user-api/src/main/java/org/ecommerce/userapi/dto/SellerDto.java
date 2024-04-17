package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.UserStatus;
import org.ecommerce.userapi.security.JwtUtils;

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
	private Boolean isDeleted;
	private LocalDateTime updateDatetime;
	private Integer beanPay;
	private UserStatus userStatus;
	private String accessToken;
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
			public static Login of(final SellerDto sellerDto) {	return new Login(JwtUtils.prefix(sellerDto.getAccessToken()));}
		}
	}

	public static class Request {
		public record Register(
			String email,
			String name,
			String password,
			String address,
			String phoneNumber
		) {
		}

		public record Login(
			String email,
			String password
		) {
		}
	}
}
