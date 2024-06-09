package org.ecommerce.productapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;

public record ProductDto
	(
		Integer id,
		ProductCategory category,
		SellerRepDto sellerRep,
		Integer favoriteCount,
		Boolean isDecaf,
		String name,
		Bean bean,
		Acidity acidity,
		String information,
		ProductStatus status,
		Boolean isCrush,
		LocalDateTime createDatetime,
		LocalDateTime updateDatetime,
		String thumbnailUrl,
		String size,
		String capacity,
		Short deliveryFee
	) {
}
