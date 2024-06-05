package org.ecommerce.productapi.dto.request;


import org.ecommerce.productapi.exception.ProductErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifyStockRequest(
	Integer productId,
	@NotNull(message = ProductErrorMessages.stockNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer requestStock
) {
}
