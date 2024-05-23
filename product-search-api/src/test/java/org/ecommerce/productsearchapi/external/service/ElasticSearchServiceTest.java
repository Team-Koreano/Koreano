package org.ecommerce.productsearchapi.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
		final List<ProductSearchDto> productSearchDtoList = elasticSearchService.suggestSearchKeyword("아메리카노");

		// then
		assertEquals(searchHits.getSearchHit(0).getContent().getId(), productSearchDtoList.get(0).getId());
		assertEquals(searchHits.getSearchHit(1).getContent().getId(), productSearchDtoList.get(1).getId());
		assertEquals(searchHits.getSearchHit(0).getContent().getName(), productSearchDtoList.get(0).getName());
		assertEquals(searchHits.getSearchHit(1).getContent().getName(), productSearchDtoList.get(1).getName());
		assertEquals(searchHits.getSearchHit(0).getContent().getPrice(), productSearchDtoList.get(0).getPrice());
		assertEquals(searchHits.getSearchHit(1).getContent().getPrice(), productSearchDtoList.get(1).getPrice());
		assertEquals(searchHits.getSearchHit(0).getContent().getBean(),
			productSearchDtoList.get(0).getBean().getCode());
		assertEquals(searchHits.getSearchHit(1).getContent().getBean(),
			productSearchDtoList.get(1).getBean().getCode());
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
