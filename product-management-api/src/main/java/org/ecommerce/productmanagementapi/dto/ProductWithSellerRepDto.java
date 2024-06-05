package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;

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
