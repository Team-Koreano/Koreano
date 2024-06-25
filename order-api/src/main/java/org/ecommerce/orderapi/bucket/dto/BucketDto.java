package org.ecommerce.orderapi.bucket.dto;

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
