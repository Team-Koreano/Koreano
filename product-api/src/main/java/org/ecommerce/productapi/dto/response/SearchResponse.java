package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productapi.document.ProductDocument;
import org.ecommerce.productapi.dto.PagedSearchDto;

public record SearchResponse(
	Long totalElements,
	Long totalPages,
	Integer currentPage,
	Integer pageSize,
	List<ContentResponse> content
) {

	public record ContentResponse(
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

		public static ContentResponse of(final ProductDocument productDocument) {
			return new ContentResponse(
				productDocument.getId(),
				productDocument.getName(),
				productDocument.getCategory(),
				productDocument.getPrice(),
				productDocument.getStock(),
				productDocument.getSellerId(),
				productDocument.getSellerName(),
				productDocument.getFavoriteCount(),
				productDocument.getIsDecaf(),
				productDocument.getAcidity(),
				productDocument.getBean(),
				productDocument.getThumbnailUrl(),
				productDocument.getCreateDatetime()
			);
		}
	}

	public static SearchResponse of(
		PagedSearchDto pagedSearchDto
	) {
		return new SearchResponse(
			pagedSearchDto.totalElements(),
			pagedSearchDto.totalPages(),
			pagedSearchDto.currentPage(),
			pagedSearchDto.pageSize(),
			pagedSearchDto.searchHits().getSearchHits().stream()
				.map(searchHit -> ContentResponse.of(searchHit.getContent()))
				.toList()
		);
	}

}