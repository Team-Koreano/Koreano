package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

public class OrderCreatedEvent extends OrderEvent {
	public OrderCreatedEvent(final Long orderId, final LocalDateTime createdAt) {
		super(orderId, createdAt);
	}
}
