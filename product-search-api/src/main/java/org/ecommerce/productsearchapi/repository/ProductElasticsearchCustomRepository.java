package org.ecommerce.productsearchapi.repository;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface ProductElasticsearchCustomRepository {
	SearchHits<ProductDocument> findProductsByNameContaining(String keyword);
}
