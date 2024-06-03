package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;

public record ProductDto
	(
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
		ProductStatus status,
		Boolean isCrush,
		LocalDateTime createDatetime,
		LocalDateTime updateDatetime,
		List<ImageDto> imageDtoList,
		String thumbnailUrl,
		String size,
		String capacity
	){
}
