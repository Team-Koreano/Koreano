package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.security.JwtUtils;

public class SellerDto {
	public static class Response {
		public record Register(
			String email,
			String name,
			String address,
			String phoneNumber
		) {
			public static Register of(final Seller seller) {
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
			public static Login of(final String accessToken) {	return new Login(JwtUtils.prefix(accessToken));}
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
