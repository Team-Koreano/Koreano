package org.ecommerce.productsearchapi.dto;

public record SellerRepDto(
	Integer id,
	String bizName
) {
	public static SellerRepDto of(final SellerRepDto sellerRepDto) {
		return new SellerRepDto(
			sellerRepDto.id(),
			sellerRepDto.bizName()
		);
	}
}
