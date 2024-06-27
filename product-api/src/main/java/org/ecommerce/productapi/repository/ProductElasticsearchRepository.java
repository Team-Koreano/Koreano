package org.ecommerce.productapi.repository;

import org.ecommerce.productapi.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticsearchRepository
	extends ElasticsearchRepository<ProductDocument, Integer>, ProductElasticsearchCustomRepository {
}
