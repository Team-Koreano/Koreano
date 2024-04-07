package org.ecommerce.productsearchapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-search")
public class ProductController {

	private final ProductService productService;

	@PostMapping("/seller")
	public Response<Void> registerSeller() {
		productService.registerSeller();
		return new Response<>(200, null);
	}

	@PostMapping("/")
	public Response<Void> createProduct(@RequestBody ProductDto.Request.CreateProductDto createProductDto) {
		productService.createProduct(createProductDto);
		return new Response<>(200, null);
	}
}
