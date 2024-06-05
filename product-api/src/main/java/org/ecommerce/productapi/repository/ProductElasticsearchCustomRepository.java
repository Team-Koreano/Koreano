package org.ecommerce.productapi.repository;

import org.ecommerce.productapi.document.ProductDocument;
import org.ecommerce.productapi.dto.PagedSearchDto;
import org.ecommerce.productapi.dto.request.SearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface ProductElasticsearchCustomRepository {
	SearchHits<ProductDocument> findProductsByNameContaining(String keyword);

	PagedSearchDto searchProducts(SearchRequest request, Pageable pageable);
}
