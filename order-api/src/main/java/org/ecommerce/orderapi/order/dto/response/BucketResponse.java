package org.ecommerce.orderapi.order.dto.response;

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
