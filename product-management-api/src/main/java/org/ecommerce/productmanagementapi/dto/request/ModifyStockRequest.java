package org.ecommerce.productmanagementapi.dto.request;

import org.ecommerce.productmanagementapi.exception.ProductManagementErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifyStockRequest(
	Integer productId,
	@NotNull(message = ProductManagementErrorMessages.stockNotNull)
	@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
	Integer requestStock
) {
}
