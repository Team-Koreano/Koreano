package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.Status;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class UserDto {

	public static class Request{
		public record CreateUserDto(
			String email,
			String name,
			String password,
			Gender gender,
			Short age,
			String phoneNumber,

			Integer beanPay,
			Status status
		){}
}
	public static class Response{

	}
}
