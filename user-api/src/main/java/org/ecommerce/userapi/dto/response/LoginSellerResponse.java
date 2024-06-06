package org.ecommerce.userapi.dto.response;

import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.provider.JwtProvider;

public record LoginSellerResponse(
	String accessToken
) {
	public static LoginSellerResponse of(final SellerDto sellerDto) {
		return new LoginSellerResponse(JwtProvider.prefix(sellerDto.accessToken()));
	}
}
