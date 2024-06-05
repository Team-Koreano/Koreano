package org.ecommerce.userapi.dto.response;

import org.ecommerce.userapi.entity.enumerated.Gender;

public record CreateUserResponse(
	String email,
	String name,
	Gender gender,
	Short age,
	String phoneNumber
) {
}