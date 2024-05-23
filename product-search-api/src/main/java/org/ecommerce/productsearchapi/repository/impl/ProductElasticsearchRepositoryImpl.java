package org.ecommerce.productsearchapi.repository.impl;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.enumerated.ProductDocumentField;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchCustomRepository;
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

@Repository
@RequiredArgsConstructor
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
	public SearchHits<ProductDocument> searchProducts(ProductSearchDto.Request.Search search, Pageable pageable) {

		// TODO  .toLowerCase() 를 es 설정으로 ABC 토큰나이저 사용 안하게 변경해야함.

		SortOptions sortOptions = SortOptionsBuilders
			.field(builder -> builder
				.field(search.sortType().getField())
				.order(SortOrder.valueOf(search.sortType().getOrderBy())));
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		if (Boolean.TRUE.equals(search.validKeyword())) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field(ProductDocumentField.NAME.getField())
				.query(search.keyword())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (Boolean.TRUE.equals(search.validIsDecaf())) {
			QueryVariant matchQuery = new TermQuery.Builder()
				.field(ProductDocumentField.IS_DECAF.getField())
				.value(search.isDecaf())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (Boolean.TRUE.equals(search.validCategory())) {
			QueryVariant matchQuery = new TermQuery.Builder()
				.field(ProductDocumentField.CATEGORY.getField())
				.value(search.category().getCode().toLowerCase())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (Boolean.TRUE.equals(search.validBean())) {
			QueryVariant matchQuery = new TermQuery.Builder()
				.field(ProductDocumentField.BEAN.getField())
				.value(search.bean().getCode().toLowerCase())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (Boolean.TRUE.equals(search.validAcidity())) {
			QueryVariant matchQuery = new TermQuery.Builder()
				.field(ProductDocumentField.ACIDITY.getField())
				.value(search.acidity().getCode().toLowerCase())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		NativeQuery query = NativeQuery.builder()
			.withQuery(boolQueryBuilder.build()._toQuery())
			.withSort(sortOptions)
			.withPageable(pageable)
			.build();

		return elasticsearchTemplate.search(query, ProductDocument.class);
	}
}
