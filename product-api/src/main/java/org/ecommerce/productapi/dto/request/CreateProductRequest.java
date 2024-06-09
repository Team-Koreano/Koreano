package org.ecommerce.productapi.dto.request;

import java.util.List;

import org.ecommerce.productapi.dto.ProductDetailDto;
import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.exception.ProductErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
	Boolean isDecaf,
	Acidity acidity,
	Bean bean,
	ProductCategory category,
	@NotBlank(message = ProductErrorMessages.informationNotBlank)
	String information,
	@NotBlank(message = ProductErrorMessages.nameNotBlank)
	String name,
	Boolean isCrush,
	String capacity,
	@Min(value = 0, message = ProductErrorMessages.isCanNotBeBelowZero)
	short deliveryFee,
	List<ProductDetailDto> productDetails
) {
}
