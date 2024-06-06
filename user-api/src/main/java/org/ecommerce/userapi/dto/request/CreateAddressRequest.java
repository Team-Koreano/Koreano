package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
	@NotBlank(message = UserErrorMessages.addressNameNotBlank)
	String name,
	@NotBlank(message = UserErrorMessages.postAddressNotBlank)
	String postAddress,
	@NotBlank(message = UserErrorMessages.addressDetailNotBlank)
	String detail
) {
}
