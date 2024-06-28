package org.ecommerce.productapi.repository.impl;

import org.ecommerce.productapi.document.ProductDocument;
import org.ecommerce.productapi.dto.PagedSearchDto;
import org.ecommerce.productapi.dto.request.SearchRequest;
import org.ecommerce.productapi.enumerated.ProductDocumentField;
import org.ecommerce.productapi.repository.ProductElasticsearchCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch._types.ScoreSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductElasticsearchRepositoryImpl implements ProductElasticsearchCustomRepository {

	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public SearchHits<ProductDocument> findProductsByNameContaining(String keyword) {

		// name 에 가중치를 둬서 일치할수록 score 를 높이는 쿼리
		QueryVariant matchQueryBoostedKeyword = new MatchQuery.Builder()
			.field(ProductDocumentField.NAME.getField())
			.query(keyword)
			.boost(2f)
			.build();

		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool()
			.should(new Query(matchQueryBoostedKeyword));

		ScoreSort.Builder scoreSort = SortOptionsBuilders
			.score()
			.order(SortOrder.Desc);

		NativeQuery query = NativeQuery.builder()
			.withQuery(boolQueryBuilder.build()._toQuery())
			.withSort(scoreSort.build()._toSortOptions())
			.build();

		return elasticsearchTemplate.search(query, ProductDocument.class);
	}

	@Override
	public PagedSearchDto searchProducts(SearchRequest search, Pageable pageable) {

		//todo: sortType 을 다른 방식으로 쉽게 사용할 방법 없나 고민.
		SortOptions sortOptions = SortOptionsBuilders
			.field(builder -> builder
				.field(search.sortType().getField())
				.order(SortOrder.valueOf(search.sortType().getOrderBy())));

		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
		BoolQuery.Builder filterQueryBuilder = QueryBuilders.bool();

		if (search.validKeyword()) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field(ProductDocumentField.NAME.getField())
				.query(search.keyword())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (search.validIsDecaf()) {
			QueryVariant termQuery = new TermQuery.Builder()
				.field(ProductDocumentField.IS_DECAF.getField())
				.value(search.isDecaf())
				.build();
			filterQueryBuilder.filter(new Query(termQuery));
		}

		if (search.validCategory()) {
			QueryVariant termQuery = new TermQuery.Builder()
				.field(ProductDocumentField.CATEGORY.getField())
				.value(search.category().getCode().toLowerCase())
				.build();
			filterQueryBuilder.filter(new Query(termQuery));
		}

		if (search.validBean()) {
			QueryVariant termQuery = new TermQuery.Builder()
				.field(ProductDocumentField.BEAN.getField())
				.value(search.bean().getCode().toLowerCase())
				.build();
			filterQueryBuilder.filter(new Query(termQuery));
		}

		if (search.validAcidity()) {
			QueryVariant termQuery = new TermQuery.Builder()
				.field(ProductDocumentField.ACIDITY.getField())
				.value(search.acidity().getCode().toLowerCase())
				.build();
			filterQueryBuilder.filter(new Query(termQuery));
		}

		NativeQuery query = NativeQuery.builder()
			.withQuery(boolQueryBuilder.build()._toQuery())
			.withFilter(filterQueryBuilder.build()._toQuery())
			.withSort(sortOptions)
			.withPageable(pageable)
			.build();
		SearchHits<ProductDocument> productDocumentSearchHits = elasticsearchTemplate.search(query, ProductDocument.class);

		return PagedSearchDto.of(
			productDocumentSearchHits.getTotalHits(),
			productDocumentSearchHits.getTotalHits() / pageable.getPageSize(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			productDocumentSearchHits
		);
	}
}
