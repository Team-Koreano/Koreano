package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

public record AccountDto(
	Integer id,
	String number,
	String bankName,
	LocalDateTime createDatetime,
	boolean isDeleted,
	LocalDateTime updateDatetime
) {
}
