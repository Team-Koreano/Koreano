package org.ecommerce.orderapi.event;

public class OrderCanceledEvent extends OrderEvent {
	public OrderCanceledEvent(final Long orderId) {
		super(orderId);
	}
}
