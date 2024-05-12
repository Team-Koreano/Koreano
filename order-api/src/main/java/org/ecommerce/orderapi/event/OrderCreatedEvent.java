package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.dto.OrderDto;

public class OrderCreatedEvent extends OrderEvent {
	public OrderCreatedEvent(OrderDto orderDto, LocalDateTime createdAt) {
		super(orderDto, createdAt);
	}
}
