package org.ecommerce.productsearchapi.repository;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, Integer> {
}
