package org.ecommerce.productsearchapi.repository;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.request.SearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface ProductElasticsearchCustomRepository {
	SearchHits<ProductDocument> findProductsByNameContaining(String keyword);

	SearchHits<ProductDocument> searchProducts(SearchRequest request, Pageable pageable);
}
