package org.ecommerce.orderapi.handler;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.event.OrderCanceledEvent;
import org.ecommerce.orderapi.service.OrderHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

	private final OrderHelper orderHelper;

	// public OrderDto createOrder(
	// 		final Integer userId,
	// 		final OrderDto.Request.Create create
	// ) {
	// 	OrderDto orderDto = orderHelper.createOrder(userId, create);
	// 	// TODO : Payment MS 결제 요청 이벤트 Publish
	// 	new OrderCreatedEvent(orderDto.getId(), LocalDateTime.now());
	// 	return orderDto;
	// }

	public OrderDto cancelOrder(
			final Integer userId,
			final Long OrderId,
			final Long OrderItemId
	) {
		OrderDto orderDto = orderHelper.cancelOrder(userId, OrderId, OrderItemId);
		// TODO : Payment MS 결제 취소 이벤트 Publish
		new OrderCanceledEvent(orderDto.getId(), LocalDateTime.now());
		return orderDto;
	}
}
