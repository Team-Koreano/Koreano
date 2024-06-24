package org.ecommerce.productapi.dto.response;

public record ProductDetailResponse(
	Integer price,
	Integer stock,
	String size,
	Boolean isDefault,
	String status
) {
}
