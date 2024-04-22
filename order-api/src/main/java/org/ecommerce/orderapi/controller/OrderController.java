package org.ecommerce.orderapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders/v1")
public class OrderController {

	private final OrderService orderService;

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
}
