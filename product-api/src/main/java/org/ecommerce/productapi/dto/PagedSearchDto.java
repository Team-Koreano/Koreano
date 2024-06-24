package org.ecommerce.productapi.dto;

import org.ecommerce.productapi.document.ProductDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public record PagedSearchDto(
	Long totalElements,
	Long totalPages,
	Integer currentPage,
	Integer pageSize,
	SearchHits<ProductDocument> searchHits
) {
	public static PagedSearchDto of(
		final Long totalElements,
		final Long totalPages,
		final Integer currentPage,
		final Integer pageSize,
		final SearchHits<ProductDocument> searchHits
	) {
		return new PagedSearchDto(totalElements, totalPages, currentPage, pageSize, searchHits);
	}
}
