package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

public record AddressDto(
	Integer id,
	String name,
	String postAddress,
	String detail,
	LocalDateTime createDatetime,
	boolean isDeleted,
	LocalDateTime updateDatetime
) {
}
