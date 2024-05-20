package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

public class OrderCanceledEvent extends OrderEvent {
	public OrderCanceledEvent(final Long orderId, final LocalDateTime createdAt) {
		super(orderId, createdAt);
	}
}
