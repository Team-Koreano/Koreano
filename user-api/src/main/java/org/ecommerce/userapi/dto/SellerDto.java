package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.Seller;

public class SellerDto {
	public static class Request {
		public record Register(
			String email,
			String name,
			String password,
			String address,
			String phoneNumber
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
			public static Register of(final Seller seller) {
				return new Register(
					seller.getEmail(),
					seller.getName(),
					seller.getAddress(),
					seller.getPhoneNumber()
				);
			}

		}
	}
}