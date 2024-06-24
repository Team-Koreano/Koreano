package org.ecommerce.productapi.dto.response;

public interface CategoryResponse {

	record BeanResponse(
		Boolean isDecaf,
		String acidity,
		String bean,
		Boolean isCrush) implements CategoryResponse {
	}

	record DefaultResponse(
		String capacity) implements CategoryResponse {
	}
}