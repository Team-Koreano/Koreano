package org.ecommerce.orderapi.order.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.order.dto.OrderDto;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.stock.dto.StockDto;
import org.ecommerce.orderapi.stock.dto.StockMapper;
import org.ecommerce.orderapi.order.handler.OrderQueryHandler;
import org.ecommerce.orderapi.order.service.OrderDomainService;
import org.ecommerce.orderapi.stock.service.StockDomainService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/orders/v1")
public class OrderController {

	private final OrderQueryHandler orderQueryHandler;
	private final StockDomainService stockDomainService;
	private final OrderDomainService orderDomainService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@PostMapping
	public Response<OrderDto.Response> createOrder(
			@RequestBody @Valid final OrderDto.Request.Create createRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.OrderDtoToResponse(
						orderDomainService.createOrder(USER_ID, createRequest)
				)
		);
	}

	@GetMapping
	public Response<List<OrderDto.Response>> getOrders(
			@RequestParam(required = false) final Integer year,
			@RequestParam(required = false, defaultValue = "0") final Integer pageNumber
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				orderQueryHandler.getOrders(USER_ID, year, pageNumber).stream()
						.map(OrderMapper.INSTANCE::OrderDtoToResponse)
						.toList()
		);
	}

	@DeleteMapping("/{orderId}/orderItems/{orderItemId}")
	public Response<OrderDto.Response> cancelOrder(
			@PathVariable("orderId") final Long orderId,
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.OrderDtoToResponse(
						orderDomainService.cancelOrder(USER_ID, orderId, orderItemId)
				)
		);
	}

	@GetMapping("/mocks/{productId}")
	public Response<StockDto.Response> getMock(
			@PathVariable("productId") final Integer productId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				StockMapper.INSTANCE.toResponse(
						stockDomainService.getMockData(productId)
				)
		);
	}

	@PostMapping("/mocks")
	public void saveMockData() {
		stockDomainService.saveMock();
	}
}
