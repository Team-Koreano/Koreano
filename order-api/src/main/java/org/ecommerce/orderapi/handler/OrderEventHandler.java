package org.ecommerce.orderapi.handler;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.event.OrderCreatedEvent;
import org.ecommerce.orderapi.service.OrderHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

	private final OrderHelper orderHelper;

	public OrderDto.Response createOrder(
			final Integer userId,
			final OrderDto.Request.Place place
	) {
		OrderDto orderDto = orderHelper.createOrder(userId, place);

		// TODO : Kafka 결제 요청 이벤트 Publish
		new OrderCreatedEvent(orderDto, LocalDateTime.now());
		return OrderMapper.INSTANCE.OrderDtoToResponse(orderDto);
	}
}
