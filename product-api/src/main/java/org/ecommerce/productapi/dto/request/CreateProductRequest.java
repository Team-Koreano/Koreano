package org.ecommerce.productapi.dto.request;

import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.exception.ProductErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
	Boolean isDecaf,
	@NotNull(message = ProductErrorMessages.priceNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer price,
	@NotNull(message = ProductErrorMessages.stockNotNull)
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	Integer stock,
	Acidity acidity,
	Bean bean,
	ProductCategory category,
	@NotBlank(message = ProductErrorMessages.informationNotBlank)
	String information,
	@NotBlank(message = ProductErrorMessages.nameNotBlank)
	String name,
	Boolean isCrush,
	String size,
	String capacity,
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	short deliveryFee
) {
}
