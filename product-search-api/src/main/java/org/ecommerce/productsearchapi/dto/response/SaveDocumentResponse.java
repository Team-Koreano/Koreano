package org.ecommerce.productsearchapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;

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

	public static SaveDocumentResponse of(final ProductDtoWithImageListDto productDtoWithImageListDto) {
		return new SaveDocumentResponse(
			productDtoWithImageListDto.id(),
			productDtoWithImageListDto.category().getTitle(),
			productDtoWithImageListDto.price(),
			productDtoWithImageListDto.stock(),
			productDtoWithImageListDto.sellerRep().id(),
			productDtoWithImageListDto.sellerRep().bizName(),
			productDtoWithImageListDto.favoriteCount(),
			productDtoWithImageListDto.isDecaf(),
			productDtoWithImageListDto.name(),
			productDtoWithImageListDto.acidity().getTitle(),
			productDtoWithImageListDto.bean().getTitle(),
			productDtoWithImageListDto.information(),
			productDtoWithImageListDto.thumbnailUrl(),
			productDtoWithImageListDto.size(),
			productDtoWithImageListDto.capacity(),
			productDtoWithImageListDto.deliveryFee(),
			productDtoWithImageListDto.createDatetime()
		);
	}
}
