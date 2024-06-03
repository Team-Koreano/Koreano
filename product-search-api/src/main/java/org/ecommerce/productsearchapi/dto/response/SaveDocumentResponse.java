package org.ecommerce.productsearchapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductDto;

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

	public static SaveDocumentResponse of(final ProductDto productDto) {
		return new SaveDocumentResponse(
			productDto.id(),
			productDto.category().getTitle(),
			productDto.price(),
			productDto.stock(),
			productDto.sellerRep().id(),
			productDto.sellerRep().bizName(),
			productDto.favoriteCount(),
			productDto.isDecaf(),
			productDto.name(),
			productDto.acidity().getTitle(),
			productDto.bean().getTitle(),
			productDto.information(),
			productDto.thumbnailUrl(),
			productDto.size(),
			productDto.capacity(),
			productDto.createDatetime()
		);
	}
}
