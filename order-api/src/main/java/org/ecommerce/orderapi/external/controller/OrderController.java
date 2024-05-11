package org.ecommerce.orderapi.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderItemDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.OrderStatusHistoryDto;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.service.OrderDomainService;
import org.ecommerce.orderapi.service.OrderHelper;
import org.ecommerce.orderapi.service.StockService;
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

	private final OrderDomainService orderDomainService;
	private final OrderHelper orderHelper;
	private final StockService stockService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@PostMapping
	public Response<OrderDto.Response> createOrder(
			@RequestBody @Valid final OrderDto.Request.Place placeRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.OrderDtoToResponse(
						orderHelper.createOrder(USER_ID, placeRequest)
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
				orderDomainService.getOrders(USER_ID, year, pageNumber).stream()
						.map(OrderMapper.INSTANCE::OrderDtoToResponse)
						.toList()
		);
	}

	@GetMapping("{orderId}/orderItems/{orderItemId}/statusHistory")
	public Response<List<OrderStatusHistoryDto.Response>> getAllOrderStatusHistory(
			@PathVariable("orderId") final Long orderId,
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				orderDomainService.getOrderStatusHistory(orderItemId).stream()
						.map(OrderMapper.INSTANCE::orderStatusHistoryDtotoResponse)
						.toList()
		);
	}

	@DeleteMapping("/{orderId}/orderItems/{orderItemId}")
	public Response<OrderItemDto.Response> cancelOrder(
			@PathVariable("orderId") final Long orderId,
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.orderItemDtoToResponse(
						orderDomainService.cancelOrder(USER_ID, orderItemId)
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
						stockService.getMockData(productId)
				)
		);
	}

	@PostMapping("/mocks")
	public void saveMockData() {
		stockService.saveMock();
	}
}
