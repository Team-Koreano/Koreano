package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.BeanPayErrorMessage.*;

import org.ecommerce.paymentapi.entity.enumerate.Role;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBeanPayRequest(
	@NotNull(message = NOT_NULL_USER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
	Integer userId,
	@NotBlank(message = NOT_BLANK_USER_ROLE)
	Role role
) {
}