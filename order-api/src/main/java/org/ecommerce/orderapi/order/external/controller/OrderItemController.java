package org.ecommerce.orderapi.order.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.order.dto.OrderItemStatusHistoryDto;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.handler.OrderItemQueryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/orderItems/v1")
public class OrderItemController {

	private final OrderItemQueryHandler orderItemQueryHandler;

	@GetMapping("/{orderItemId}/statusHistories")
	public Response<OrderItemStatusHistoryDto.Response> getOrderItemStatusHistories(
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.orderItemStatusHistoryDtotoResponse(
						orderItemQueryHandler.getOrderItemStatusHistories(orderItemId)
				)
		);
	}
}
