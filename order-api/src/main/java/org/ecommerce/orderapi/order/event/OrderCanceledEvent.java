package org.ecommerce.orderapi.order.event;

public class OrderCanceledEvent extends OrderEvent {
	public OrderCanceledEvent(final Long orderId) {
		super(orderId);
	}
}
