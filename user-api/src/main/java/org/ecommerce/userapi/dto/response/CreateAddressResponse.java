package org.ecommerce.userapi.dto.response;

public record CreateAddressResponse(
	Integer id,
	String name,
	String postAddress,
	String detail
) {

}
