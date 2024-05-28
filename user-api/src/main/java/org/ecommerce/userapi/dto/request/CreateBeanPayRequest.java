package org.ecommerce.userapi.dto.request;

import org.ecommerce.userapi.entity.enumerated.Role;

public record CreateBeanPayRequest(
	Integer id,
	Role role
) {
}
