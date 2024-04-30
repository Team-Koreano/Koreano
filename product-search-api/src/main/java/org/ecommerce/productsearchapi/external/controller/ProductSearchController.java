package org.ecommerce.productsearchapi.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.external.service.ProductSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/product-search/v1")
public class ProductSearchController {

	private final ProductSearchService productSearchService;

	@GetMapping("/{productId}")
	public Response<ProductSearchDto.Response.Detail> getProductById(
		@PathVariable("productId") final Integer productId) {

		return new Response<>(HttpStatus.OK.value(),
			ProductSearchDto.Response.Detail.of(productSearchService.getProductById(productId))
		);
	}

}
