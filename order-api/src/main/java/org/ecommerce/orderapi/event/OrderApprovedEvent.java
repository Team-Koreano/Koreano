package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

public class OrderApprovedEvent extends OrderEvent {
	public OrderApprovedEvent(final Long orderId, final LocalDateTime createdAt) {
		super(orderId, createdAt);
	}
}
