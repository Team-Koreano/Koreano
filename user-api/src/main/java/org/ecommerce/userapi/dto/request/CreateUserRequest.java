package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
	@NotBlank(message = UserErrorMessages.emailNotBlank)
	@Email
	String email,
	@NotBlank(message = UserErrorMessages.nameNotBlank)
	String name,
	@NotBlank(message = UserErrorMessages.passwordNotBlank)
	String password,
	Gender gender,
	@NotNull(message = UserErrorMessages.ageNotNull)
	Short age,
	@NotBlank(message = UserErrorMessages.phoneNumberNotBlank)
	String phoneNumber
) {

}
