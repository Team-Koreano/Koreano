package org.ecommerce.orderapi.event;

public class OrderCreatedEvent extends OrderEvent {
	public OrderCreatedEvent(final Long orderId) {
		super(orderId);
	}
}
