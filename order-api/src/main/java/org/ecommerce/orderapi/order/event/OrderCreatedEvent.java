package org.ecommerce.orderapi.order.event;

public class OrderCreatedEvent extends OrderEvent {
	public OrderCreatedEvent(final Long orderId) {
		super(orderId);
	}
}
