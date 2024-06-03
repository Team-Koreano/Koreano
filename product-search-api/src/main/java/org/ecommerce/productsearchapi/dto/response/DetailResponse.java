package org.ecommerce.productsearchapi.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;

public record DetailResponse(
	Integer id,
	Boolean isDecaf,
	Integer price,
	Integer sellerId,
	String sellerName,
	Integer stock,
	String acidity,
	String bean,
	String category,
	String information,
	String name,
	String status,
	Boolean isCrush,
	Integer favoriteCount,
	LocalDateTime createDatetime,
	LinkedList<ImageDto> imageDtoList
) {
	public static DetailResponse of(final ProductDtoWithImageListDto productDtoWithImageListDto) {
		return new DetailResponse(
			productDtoWithImageListDto.id(),
			productDtoWithImageListDto.isDecaf(),
			productDtoWithImageListDto.price(),
			productDtoWithImageListDto.sellerRep().id(),
			productDtoWithImageListDto.sellerRep().bizName(),
			productDtoWithImageListDto.stock(),
			productDtoWithImageListDto.acidity().getTitle(),
			productDtoWithImageListDto.bean().getTitle(),
			productDtoWithImageListDto.category().getTitle(),
			productDtoWithImageListDto.information(),
			productDtoWithImageListDto.name(),
			productDtoWithImageListDto.status().getTitle(),
			productDtoWithImageListDto.isCrush(),
			productDtoWithImageListDto.favoriteCount(),
			productDtoWithImageListDto.createDatetime(),
			productDtoWithImageListDto.imageDtoList()
				.stream()
				.sorted(Comparator.comparingInt(ImageDto::sequenceNumber))
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
}
