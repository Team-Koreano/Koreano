package org.ecommerce.productsearchapi.internal.controller;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.internal.service.ProductSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController("internalProductSearchController")
@RequiredArgsConstructor
@RequestMapping("/api/internal/product/v1")
public class ProductSearchController {

	private final ProductSearchService productSearchService;

	@PostMapping
	public ProductSearchDto.Response.SavedProduct saveRecord(@RequestBody Product product) {
		return ProductSearchDto.Response.SavedProduct.of(productSearchService.saveProduct(product));
	}

}