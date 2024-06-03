package org.ecommerce.productsearchapi.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductDto;

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
	public static DetailResponse of(final ProductDto productDto) {
		return new DetailResponse(
			productDto.id(),
			productDto.isDecaf(),
			productDto.price(),
			productDto.sellerRep().id(),
			productDto.sellerRep().bizName(),
			productDto.stock(),
			productDto.acidity().getTitle(),
			productDto.bean().getTitle(),
			productDto.category().getTitle(),
			productDto.information(),
			productDto.name(),
			productDto.status().getTitle(),
			productDto.isCrush(),
			productDto.favoriteCount(),
			productDto.createDatetime(),
			productDto.imageDtoList()
				.stream()
				.sorted(Comparator.comparingInt(ImageDto::sequenceNumber))
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
}
