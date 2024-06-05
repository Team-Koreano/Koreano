package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record WithdrawalUserRequest(
	@Email
	@NotBlank(message = UserErrorMessages.emailNotBlank)
	String email,
	@NotBlank(message = UserErrorMessages.passwordNotBlank)
	String password,
	@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
	String phoneNumber
) {
}
