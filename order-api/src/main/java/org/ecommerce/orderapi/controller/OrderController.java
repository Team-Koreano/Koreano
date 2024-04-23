package org.ecommerce.orderapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderDto;
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

	@PostMapping
	public Response<OrderDto.Response> createOrder(
			@RequestBody @Valid final OrderDto.Request.Create createRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderDto.Response.of(
						null
				)
		);
	}
}
