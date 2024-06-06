package org.ecommerce.productmanagementapi.dto.request;

import java.util.List;

import org.ecommerce.product.entity.enumerated.ProductStatus;

public record ModifyProductsStatusRequest(
	List<Integer> productId,
	ProductStatus productStatus
) {
}
