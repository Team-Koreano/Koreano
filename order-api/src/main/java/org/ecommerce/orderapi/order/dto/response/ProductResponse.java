package org.ecommerce.orderapi.order.dto.response;

import org.ecommerce.orderapi.order.entity.enumerated.ProductStatus;

public record ProductResponse(
		Integer id,
		String name,
		Integer price,
		Integer sellerId,
		String sellerName,
		ProductStatus status
) {
}
