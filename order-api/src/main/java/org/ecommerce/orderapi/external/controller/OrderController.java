package org.ecommerce.orderapi.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.ProductDto;
import org.ecommerce.orderapi.service.OrderService;
import org.ecommerce.orderapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("externalOrderController")
@RequiredArgsConstructor
@RequestMapping("/api/external/orders/v1")
public class OrderController {

	private final OrderService orderService;
	private final ProductService productService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@PostMapping
	public Response<OrderDto.Response> createOrder(
			@RequestBody @Valid final OrderDto.Request.Place placeRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.toResponse(
						orderService.placeOrder(USER_ID, placeRequest)
				)
		);
	}

	@GetMapping("/mocks/{productId}")
	public Response<ProductDto> getMock(
			@PathVariable("productId") final Integer productId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				productService.getMockData(productId)
		);
	}

	@PostMapping("/mocks")
	public void saveMockData() {
		productService.saveMock();
	}
}