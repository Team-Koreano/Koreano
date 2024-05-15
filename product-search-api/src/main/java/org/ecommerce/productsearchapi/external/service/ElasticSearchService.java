package org.ecommerce.productsearchapi.external.service;

import java.util.List;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.springframework.data.domain.Pageable;
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
	 * @return List<ProductSearchDto>
	 */

	public List<ProductSearchDto> suggestSearchKeyword(final String keyword) {
		final SearchHits<ProductDocument> searchHits = productRepository.findProductsByNameContaining(keyword);

		return searchHits.stream()
			.map(SearchHit::getContent)
			.map(ProductMapper.INSTANCE::documentToDto)
			.toList();
	}

	/**
	 *
	 * @author ${no.oneho}
	 * 여러 조건을 통해 상품 리스트 검색
	 * @param request search 조건이 들어갈 Dto
	 * @param pageSize page 의 사이즈
	 * @param pageNumber page 의 넘버
	 * @return List<ProductSearchDto>
	 */

	public List<ProductSearchDto> searchProducts(ProductSearchDto.Request.Search request, Integer pageNumber, Integer pageSize) {
		final Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
		final SearchHits<ProductDocument> searchHits = productRepository.searchProducts(request, pageable);

		return searchHits.stream()
			.map(SearchHit::getContent)
			.map(ProductMapper.INSTANCE::documentToDto)
			.toList();
	}
}
