package org.ecommerce.productapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;

public record ProductWithSellerRepAndImagesDto(
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
	String thumbnailUrl,
	String size,
	String capacity,
	LocalDateTime createDatetime,
	LocalDateTime updateDatetime,
	Short deliveryFee,
	List<ImageDto> images
) {
}
