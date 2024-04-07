package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.UserStatus;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class UserDto {

	public static class Request{
		public record Register(
			String email,
			String name,
			String password,
			Gender gender,
			Short age,
			String phoneNumber,

			Integer beanPay,
			UserStatus userStatus
		){}
}
	public static class Response{

	}
}
