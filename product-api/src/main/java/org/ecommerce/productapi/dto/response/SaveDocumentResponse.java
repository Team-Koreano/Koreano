package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productapi.dto.ImageDto;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesAndProductDetailsDto;

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

	public static SaveDocumentResponse of(
		final ProductWithSellerRepAndImagesAndProductDetailsDto productWithSellerRepAndImagesAndProductDetailsDto) {
		return new SaveDocumentResponse(
			productWithSellerRepAndImagesAndProductDetailsDto.id(),
			productWithSellerRepAndImagesAndProductDetailsDto.category().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.price(),
			productWithSellerRepAndImagesAndProductDetailsDto.stock(),
			productWithSellerRepAndImagesAndProductDetailsDto.sellerRep().id(),
			productWithSellerRepAndImagesAndProductDetailsDto.sellerRep().bizName(),
			productWithSellerRepAndImagesAndProductDetailsDto.favoriteCount(),
			productWithSellerRepAndImagesAndProductDetailsDto.isDecaf(),
			productWithSellerRepAndImagesAndProductDetailsDto.name(),
			productWithSellerRepAndImagesAndProductDetailsDto.acidity().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.bean().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.information(),
			productWithSellerRepAndImagesAndProductDetailsDto.thumbnailUrl(),
			productWithSellerRepAndImagesAndProductDetailsDto.size(),
			productWithSellerRepAndImagesAndProductDetailsDto.capacity(),
			productWithSellerRepAndImagesAndProductDetailsDto.deliveryFee(),
			productWithSellerRepAndImagesAndProductDetailsDto.createDatetime()
		);
	}
}
