package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
	@NotBlank(message = UserErrorMessages.bankNumberNotBlank)
	String number,
	@NotBlank(message = UserErrorMessages.bankNameNotEmpty)
	String bankName
) {
}
