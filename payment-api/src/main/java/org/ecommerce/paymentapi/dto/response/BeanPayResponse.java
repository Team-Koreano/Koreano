package org.ecommerce.paymentapi.dto.response;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.Role;

public record BeanPayResponse(
	Integer id,
	Integer userId,
	Role role,
	Integer amount,
	LocalDateTime createDateTime
) {
}