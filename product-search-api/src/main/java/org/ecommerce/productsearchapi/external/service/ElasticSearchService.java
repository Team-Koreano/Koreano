package org.ecommerce.productsearchapi.external.service;

import java.util.List;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

	private final ProductElasticsearchRepository productRepository;


	/**
	 * @author ${no.oneho}
	 * 특정 키워드가 포함된 상품 리스트를 추려낸다.
	 * @param  keyword 검색 키워드
	 * @return ProductSearchDto
	 */

	public List<ProductSearchDto> suggestSearchKeyword(final String keyword) {
		final SearchHits<ProductDocument> searchHits = productRepository.findProductsByNameContaining(keyword);

		return searchHits.stream()
			.map(SearchHit::getContent)
			.map(ProductMapper.INSTANCE::documentToDto)
			.toList();
	}

}
