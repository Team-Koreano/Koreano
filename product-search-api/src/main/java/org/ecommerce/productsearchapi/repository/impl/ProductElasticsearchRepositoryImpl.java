package org.ecommerce.productsearchapi.repository.impl;

import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
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
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductElasticsearchRepositoryImpl implements ProductElasticsearchCustomRepository {

	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public SearchHits<ProductDocument> findProductsByNameContaining(String keyword) {

		// name 에 가중치를 둬서 일치할수록 score 를 높이는 쿼리
		QueryVariant matchQueryBoostedKeyword = new MatchQuery.Builder()
			.field("name")
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

		SortOptions sortOptions = SortOptionsBuilders
			.field(builder -> builder
				.field(search.sortType().getTitle())
				.order(SortOrder.Desc));

		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		if (!search.keyword().trim().isEmpty()) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field("name")
				.query(search.keyword())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (search.isDecaf() != null) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field("isDecaf")
				.query(search.isDecaf())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (search.category() != null) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field("category")
				.query(search.category().getCode())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (search.bean() != null) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field("bean")
				.query(search.bean().getCode())
				.build();
			boolQueryBuilder.must(new Query(matchQuery));
		}

		if (search.acidity() != null) {
			QueryVariant matchQuery = new MatchQuery.Builder()
				.field("acidity")
				.query(search.acidity().getTitle())
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
