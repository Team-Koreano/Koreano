package org.ecommerce.userapi.dto.response;

public record CreateSellerResponse(
	String email,
	String name,
	String address,
	String phoneNumber
) {
}
