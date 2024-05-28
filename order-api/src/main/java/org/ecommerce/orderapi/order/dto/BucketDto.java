package org.ecommerce.orderapi.order.dto;

import java.time.LocalDate;

public record BucketDto(
		Long id,
		Integer userId,
		String seller,
		Integer productId,
		Integer quantity,
		LocalDate createDate
) {
}
