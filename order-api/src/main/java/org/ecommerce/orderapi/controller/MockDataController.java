package org.ecommerce.orderapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.ProductDto;
import org.ecommerce.orderapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/mocks/v1")
public class MockDataController {

	private final ProductService productService;

	@PostMapping
	public void saveMockData() {
		productService.saveMock();
	}

	@GetMapping("/{productId}")
	public Response<ProductDto> getMock(
			@PathVariable("productId") final Integer productId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				productService.getMockData(productId)
		);
	}
}
