package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateSellerRequest(
	@NotBlank(message = UserErrorMessages.emailNotBlank)
	@Email
	String email,
	@NotBlank(message = UserErrorMessages.nameNotBlank)
	String name,
	@NotBlank(message = UserErrorMessages.passwordNotBlank)
	String password,
	@NotBlank(message = UserErrorMessages.addressNotBlank)
	String address,
	@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
	String phoneNumber
) {
}
