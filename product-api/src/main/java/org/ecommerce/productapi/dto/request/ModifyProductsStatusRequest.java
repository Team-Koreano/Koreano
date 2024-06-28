package org.ecommerce.productapi.dto.request;

import java.util.List;

import org.ecommerce.productapi.entity.enumerated.ProductStatus;

public record ModifyProductsStatusRequest(
	List<Integer> productId,
	ProductStatus productStatus
) {
}
