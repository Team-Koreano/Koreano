package org.ecommerce.orderapi.order.event;

import lombok.Getter;

@Getter
public class OrderCanceledEvent extends OrderEvent {
	private final Long orderItemId;

	public OrderCanceledEvent(final Long orderItemId) {
		super(null);
		this.orderItemId = orderItemId;
	}
}
