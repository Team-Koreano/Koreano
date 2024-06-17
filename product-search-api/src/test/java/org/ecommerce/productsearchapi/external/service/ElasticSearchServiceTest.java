package org.ecommerce.productsearchapi.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.PagedSearchDto;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.dto.request.SearchRequest;
import org.ecommerce.productsearchapi.enumerated.ProductSortType;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchServiceTest {

	LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2024, 4, 22, 3, 23, 1);

	@InjectMocks
	private ElasticSearchService elasticSearchService;
	@Mock
	private ProductElasticsearchRepository productElasticsearchRepository;

	@Test
	void 검색어_제안() {
		// given
		final SearchHits<ProductDocument> searchHits = getSearchHits();

		given(productElasticsearchRepository.findProductsByNameContaining("아메리카노")).willReturn(searchHits);

		// when
		final List<ProductDto> productDtoList = elasticSearchService.suggestSearchKeyword("아메리카노");

		// then
		assertEquals(searchHits.getSearchHit(0).getContent().getId(), productDtoList.get(0).id());
		assertEquals(searchHits.getSearchHit(1).getContent().getId(), productDtoList.get(1).id());
		assertEquals(searchHits.getSearchHit(0).getContent().getName(), productDtoList.get(0).name());
		assertEquals(searchHits.getSearchHit(1).getContent().getName(), productDtoList.get(1).name());
		assertEquals(searchHits.getSearchHit(0).getContent().getPrice(), productDtoList.get(0).price());
		assertEquals(searchHits.getSearchHit(1).getContent().getPrice(), productDtoList.get(1).price());
		assertEquals(searchHits.getSearchHit(0).getContent().getBean(),
			productDtoList.get(0).bean().getCode());
		assertEquals(searchHits.getSearchHit(1).getContent().getBean(),
			productDtoList.get(1).bean().getCode());
	}

	@Test
	void 상품_리스트_검색() {

		//given
		final SearchHits<ProductDocument> searchHits = getSearchHits();
		final SearchRequest request = new SearchRequest("아메리카노", true, ProductCategory.BEAN, Bean.ARABICA, Acidity.MEDIUM, ProductSortType.NEWEST);
		final Pageable pageable = Pageable.ofSize(2).withPage(0);

		final PagedSearchDto givenPagedSearchDto = PagedSearchDto.of(2L, 1L, 0, 2, searchHits);


		given(productElasticsearchRepository.searchProducts(request, pageable)).willReturn(givenPagedSearchDto);

		//when
		final PagedSearchDto whenPagedSearchDto = elasticSearchService.searchProducts(request, 0, 2);

		//then
		assertEquals(givenPagedSearchDto.currentPage(), whenPagedSearchDto.currentPage());
		assertEquals(givenPagedSearchDto.pageSize(), whenPagedSearchDto.pageSize());
		assertEquals(givenPagedSearchDto.totalPages(), whenPagedSearchDto.totalPages());
		assertEquals(givenPagedSearchDto.totalElements(), whenPagedSearchDto.totalElements());
		assertEquals(givenPagedSearchDto.searchHits().getSearchHit(0).getContent().getId(), whenPagedSearchDto.searchHits().getSearchHit(0).getContent().getId()
		);

	}

	private SearchHits<ProductDocument> getSearchHits() {
		ProductDocument productDocument1 = new ProductDocument(1, "BEAN", 30000, 100, 1, "커피천국", 10, false,
			"[특가 EVENT]&아메리카노 원두&세상에서 제일 존맛 커피", "MEDIUM", "ARABICA", "커피천국에서만 만나볼 수 있는 특별한 커피", "http://img123.com",
			"size", "capacity", TEST_DATE_TIME);
		ProductDocument productDocument2 = new ProductDocument(2, "BEAN", 30000, 100, 1, "커피천국", 10, false,
			"아메리카노 아무나 사먹어", "MEDIUM", "ARABICA", "커피천국에서만 만나볼 수 있는 특별한 커피", "http://img123.com", "size", "capacity", TEST_DATE_TIME);

		// SearchHit mock 생성
		SearchHit<ProductDocument> searchHit1 = new SearchHit<>("1", "1", null, 1.0f, null, null, null, null, null,
			null, productDocument1);
		SearchHit<ProductDocument> searchHit2 = new SearchHit<>("2", "2", null, 2.0f, null, null, null, null, null,
			null, productDocument2);

		return new SearchHitsImpl<>(1L, TotalHitsRelation.EQUAL_TO, 1.0f, null, null, List.of(searchHit1, searchHit2),
			null, null);
	}
}