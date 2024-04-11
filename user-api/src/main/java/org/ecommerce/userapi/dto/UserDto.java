package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Gender;

public class UserDto {

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
	}

	public static class Response {
		public record Register(
			String email,
			String name,
			Gender gender,
			Short age,
			String phoneNumber
		) {

			public static Register of(final Users users) {
				return new Register(
					users.getEmail(),
					users.getName(),
					users.getGender(),
					users.getAge(),
					users.getPhoneNumber()
				);
			}

		}
	}
}
