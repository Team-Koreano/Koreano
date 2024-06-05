package org.ecommerce.userapi.dto.response;

public record CreateAccountResponse(
	Integer id,
	String number,
	String bankName) {
}

