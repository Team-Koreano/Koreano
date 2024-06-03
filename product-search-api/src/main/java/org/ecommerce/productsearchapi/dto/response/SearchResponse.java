package org.ecommerce.productsearchapi.dto.response;

import java.time.LocalDateTime;

import org.ecommerce.productsearchapi.dto.ProductDto;

public record SearchResponse(
	Integer id,
	String name,
	String category,
	Integer price,
	Integer stock,
	Integer sellerId,
	String sellerName,
	Integer favoriteCount,
	Boolean isDecaf,
	String acidity,
	String bean,
	String thumbnailUrl,
	LocalDateTime createDatetime
) {
	public static SearchResponse of(final ProductDto productDto) {
		return new SearchResponse(
			productDto.id(),
			productDto.name(),
			productDto.category().getTitle(),
			productDto.price(),
			productDto.stock(),
			productDto.sellerRep().id(),
			productDto.sellerRep().bizName(),
			productDto.favoriteCount(),
			productDto.isDecaf(),
			productDto.acidity().getTitle(),
			productDto.bean().getTitle(),
			productDto.thumbnailUrl(),
			productDto.createDatetime()
		);
	}
}
