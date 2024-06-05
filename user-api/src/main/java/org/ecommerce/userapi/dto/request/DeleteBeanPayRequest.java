package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DeleteBeanPayRequest(
	@NotNull(message = UserErrorMessages.IdIsNotNull)
	@Min(value = 1, message = UserErrorMessages.IsCanNotBeBelowZero)
	Integer id,
	Role role
) {
}
