package org.ecommerce.orderapi.bucket.dto.response;

import java.time.LocalDate;

public record BucketResponse(
		Long id,
		Integer userId,
		String seller,
		Integer productId,
		Integer quantity,
		LocalDate createDate
) {
}
