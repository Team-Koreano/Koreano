package org.ecommerce.productapi.dto;

import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.exception.ProductErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductDetailDto(
	@NotNull(message = ProductErrorMessages.priceNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer price,
	@NotNull(message = ProductErrorMessages.stockNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer stock,
	String size,
	Boolean isDefault,
	ProductStatus status
) {
}
