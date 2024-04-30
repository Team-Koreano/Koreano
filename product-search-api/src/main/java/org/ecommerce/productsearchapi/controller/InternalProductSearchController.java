package org.ecommerce.productsearchapi.controller;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.service.ProductSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/product-search/v1")
public class InternalProductSearchController {

	private final ProductSearchService productSearchService;

	@PostMapping
	public ProductSearchDto.Response.SavedProduct saveRecord(@RequestBody Product product) {
		return ProductSearchDto.Response.SavedProduct.of(productSearchService.saveProduct(product));
	}

}