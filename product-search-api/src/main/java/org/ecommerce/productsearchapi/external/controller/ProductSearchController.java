package org.ecommerce.productsearchapi.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productsearchapi.dto.PagedSearchDto;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.dto.request.SearchRequest;
import org.ecommerce.productsearchapi.dto.response.DetailResponse;
import org.ecommerce.productsearchapi.dto.response.SearchResponse;
import org.ecommerce.productsearchapi.dto.response.SuggestedResponse;
import org.ecommerce.productsearchapi.external.service.ElasticSearchService;
import org.ecommerce.productsearchapi.external.service.ProductSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/product/v1")
public class ProductSearchController {

	private final ProductSearchService productSearchService;
	private final ElasticSearchService elasticSearchService;

	@GetMapping("/{productId}")
	public Response<DetailResponse> getProductById(
		@PathVariable("productId") final Integer productId) {

		return new Response<>(HttpStatus.OK.value(),
			DetailResponse.of(productSearchService.getProductById(productId))
		);
	}

	@GetMapping("/suggest")
	public Response<List<SuggestedResponse>> suggestSearchKeyword(
		@RequestParam(value = "keyword") final String keyword) {

		final List<ProductDto> suggestedProducts = elasticSearchService.suggestSearchKeyword(keyword);

		return new Response<>(HttpStatus.OK.value(),
			suggestedProducts.stream()
				.map(SuggestedResponse::of)
				.toList());
	}

	@GetMapping("/search")
	public Response<SearchResponse> searchProducts(
		SearchRequest request,
		@RequestParam(required = false, defaultValue = "0", name = "pageNumber")
		Integer pageNumber,
		@RequestParam(required = false, defaultValue = "10", name = "pageSize")
		Integer pageSize
		) {

		final PagedSearchDto pagedSearchDto = elasticSearchService.searchProducts(request, pageNumber, pageSize);

		return new Response<>(HttpStatus.OK.value(),
			SearchResponse.of(pagedSearchDto));
	}

}
