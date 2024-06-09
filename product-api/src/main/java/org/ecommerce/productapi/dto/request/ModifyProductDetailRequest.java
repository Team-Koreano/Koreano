package org.ecommerce.productapi.dto.request;

import org.ecommerce.productapi.exception.ProductErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifyProductDetailRequest(
	@NotNull(message = ProductErrorMessages.priceNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer price,
	String size,
	Boolean isDefault
) {
}
