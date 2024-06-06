package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
	@Email
	@NotBlank(message = UserErrorMessages.emailNotBlank)
	String email,
	@NotBlank(message = UserErrorMessages.passwordNotBlank)
	String password
) {
}
