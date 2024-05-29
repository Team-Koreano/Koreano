package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.enumerate.Role;

public record DeleteBeanPayRequest(
	Integer userId,
	Role role
) {
}
