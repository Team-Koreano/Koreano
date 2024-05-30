package org.ecommerce.orderapi.stock.event;

import java.util.Set;

import lombok.Getter;

@Getter
public class StockDecreasedEvent extends StockEvent {
	private final Long orderId;
	private final Set<Long> orderItemIds;

	public StockDecreasedEvent(
			final Long orderId,
			final Set<Long> orderItemIds
	) {
		super(null);
		this.orderId = orderId;
		this.orderItemIds = orderItemIds;
	}
}