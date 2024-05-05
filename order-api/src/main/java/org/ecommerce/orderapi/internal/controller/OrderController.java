package org.ecommerce.orderapi.internal.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.dto.OrderDetailDto;
import org.ecommerce.orderapi.dto.OrderDetailMapper;
import org.ecommerce.orderapi.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/orders/v1")
public class OrderController {

	private final StockService stockService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@PutMapping("/{orderId}/stocks")
	public Response<List<OrderDetailDto.Response>> decreaseStocks(
			@PathVariable("orderId") final Long orderId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				stockService.decreaseStocks(orderId).stream()
						.map(OrderDetailMapper.INSTANCE::toResponse)
						.toList()
		);
	}
}
