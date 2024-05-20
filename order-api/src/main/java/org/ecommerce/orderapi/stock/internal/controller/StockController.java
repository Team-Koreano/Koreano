package org.ecommerce.orderapi.stock.internal.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.stock.dto.StockDto;
import org.ecommerce.orderapi.stock.dto.StockMapper;
import org.ecommerce.orderapi.stock.handler.StockEventHandler;
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

	private final StockEventHandler stockEventHandler;

	@PutMapping("/decrease/orders/{orderId}")
	public Response<List<StockDto.Response>> decreaseStocks(
			@PathVariable("orderId") final Long orderId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				stockEventHandler.decreaseStocks(orderId).stream()
						.map(StockMapper.INSTANCE::toResponse)
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
						stockEventHandler.increaseStock(orderId, orderItemId)
				)
		);
	}
}
