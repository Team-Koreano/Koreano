package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productapi.dto.ImageDto;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesDto;

import com.google.common.annotations.VisibleForTesting;

public record SaveDocumentResponse(
	Integer id,
	String category,
	Integer price,
	Integer stock,
	Integer sellerId,
	String sellerName,
	Integer favoriteCount,
	Boolean isDecaf,
	String name,
	String acidity,
	String bean,
	String information,
	String thumbnailUrl,
	String size,
	String capacity,
	Short deliveryFee,
	LocalDateTime createDatetime
) {
	@VisibleForTesting
	public static String getThumbnailUrl(List<ImageDto> images) {
		return images.stream()
			.filter(ImageDto::isThumbnail)
			.findFirst()
			.map(ImageDto::imageUrl)
			.orElse(null);
	}

	public static SaveDocumentResponse of(final ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto) {
		return new SaveDocumentResponse(
			productWithSellerRepAndImagesDto.id(),
			productWithSellerRepAndImagesDto.category().getTitle(),
			productWithSellerRepAndImagesDto.price(),
			productWithSellerRepAndImagesDto.stock(),
			productWithSellerRepAndImagesDto.sellerRep().id(),
			productWithSellerRepAndImagesDto.sellerRep().bizName(),
			productWithSellerRepAndImagesDto.favoriteCount(),
			productWithSellerRepAndImagesDto.isDecaf(),
			productWithSellerRepAndImagesDto.name(),
			productWithSellerRepAndImagesDto.acidity().getTitle(),
			productWithSellerRepAndImagesDto.bean().getTitle(),
			productWithSellerRepAndImagesDto.information(),
			productWithSellerRepAndImagesDto.thumbnailUrl(),
			productWithSellerRepAndImagesDto.size(),
			productWithSellerRepAndImagesDto.capacity(),
			productWithSellerRepAndImagesDto.deliveryFee(),
			productWithSellerRepAndImagesDto.createDatetime()
		);
	}
}
