package org.ecommerce.orderapi.internal.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderItemDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/stocks/v1")
public class StockController {

	private final StockService stockService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@PutMapping("/decrease/orders/{orderId}")
	public Response<List<OrderItemDto.Response>> decreaseStocks(
			@PathVariable("orderId") final Long orderId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				stockService.decreaseStocks(orderId).stream()
						.map(OrderMapper.INSTANCE::orderItemDtoToResponse)
						.toList()
		);
	}

	@PutMapping("/increase/orders/{orderId}/orderItems/{orderItemId}")
	public Response<StockDto.Response> increaseStock(
			@PathVariable("orderId") final Long orderId,
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				StockMapper.INSTANCE.toResponse(
						stockService.increaseStock(orderItemId)
				)
		);
	}
}
