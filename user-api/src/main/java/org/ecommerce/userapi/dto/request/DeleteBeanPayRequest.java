package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.entity.enumerated.Role;

public record DeleteBeanPayRequest(
	Integer id,
	Role role
) {
}
