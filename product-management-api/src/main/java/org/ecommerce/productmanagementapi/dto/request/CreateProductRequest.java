package org.ecommerce.productmanagementapi.dto.request;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
	Boolean isDecaf,
	@NotNull(message = ProductManagementErrorMessages.priceNotNull)
	@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
	Integer price,
	@NotNull(message = ProductManagementErrorMessages.stockNotNull)
	@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
	Integer stock,
	Acidity acidity,
	Bean bean,
	ProductCategory category,
	@NotBlank(message = ProductManagementErrorMessages.informationNotBlank)
	String information,
	@NotBlank(message = ProductManagementErrorMessages.nameNotBlank)
	String name,
	Boolean isCrush,
	String size,
	String capacity,
	@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
	short deliveryFee
) {
}
