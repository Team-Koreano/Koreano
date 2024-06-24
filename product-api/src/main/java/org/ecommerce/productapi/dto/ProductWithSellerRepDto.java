package org.ecommerce.productapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;

public record ProductWithSellerRepDto(
	Integer id,
	ProductCategory category,
	Integer price,
	Integer stock,
	SellerRepDto sellerRep,
	Integer favoriteCount,
	Boolean isDecaf,
	String name,
	Bean bean,
	Acidity acidity,
	String information,
	Boolean isCrush,
	ProductStatus status,
	String size,
	String capacity,
	LocalDateTime createDatetime,
	LocalDateTime updateDatetime
) {
}
