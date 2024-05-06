package org.ecommerce.productsearchapi.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.external.service.EsProductSearchService;
import org.ecommerce.productsearchapi.external.service.JpaProductSearchService;
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

	private final JpaProductSearchService jpaProductSearchService;
	private final EsProductSearchService esProductSearchService;

	@GetMapping("/{productId}")
	public Response<ProductSearchDto.Response.Detail> getProductById(
		@PathVariable("productId") final Integer productId) {

		return new Response<>(HttpStatus.OK.value(),
			ProductSearchDto.Response.Detail.of(jpaProductSearchService.getProductById(productId))
		);
	}

	@GetMapping("/suggest")
	public Response<List<ProductSearchDto.Response.SuggestedProducts>> suggestSearchKeyword(
		@RequestParam final String keyword) {

		final List<ProductSearchDto> suggestedProducts = esProductSearchService.suggestSearchKeyword(keyword);

		return new Response<>(HttpStatus.OK.value(),
			suggestedProducts.stream()
				.map(ProductSearchDto.Response.SuggestedProducts::of)
				.toList());
	}

}
