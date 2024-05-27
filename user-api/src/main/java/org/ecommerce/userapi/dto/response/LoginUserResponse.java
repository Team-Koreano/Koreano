package org.ecommerce.userapi.dto.response;

import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.provider.JwtProvider;

public record LoginUserResponse(
	String accessToken
) {
	public static LoginUserResponse of(final UserDto userDto) {
		return new LoginUserResponse(JwtProvider.prefix(userDto.accessToken()));
	}
}
