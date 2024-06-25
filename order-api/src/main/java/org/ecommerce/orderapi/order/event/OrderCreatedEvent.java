package org.ecommerce.orderapi.order.event;

import java.util.List;

import lombok.Getter;

@Getter
public class OrderCreatedEvent extends OrderEvent {
	private final List<Long> bucketIds;

	public OrderCreatedEvent(final Long orderId, final List<Long> bucketIds) {
		super(orderId);
		this.bucketIds = bucketIds;
	}
}
