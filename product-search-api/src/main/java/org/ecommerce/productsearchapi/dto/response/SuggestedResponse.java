package org.ecommerce.productsearchapi.dto.response;

import org.ecommerce.productsearchapi.dto.ProductDto;

public record SuggestedResponse(
	Integer id,
	String name
) {
	public static SuggestedResponse of(final ProductDto productDto) {
		return new SuggestedResponse(
			productDto.id(),
			productDto.name()
		);
	}
}
