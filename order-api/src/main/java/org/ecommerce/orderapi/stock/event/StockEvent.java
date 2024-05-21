package org.ecommerce.orderapi.stock.event;

import org.ecommerce.orderapi.order.event.DomainEvent;

public abstract class StockEvent implements DomainEvent<Integer> {
	// TODO : 보내줄 정보를 정해야 함 (Payload or Id)
	private final Integer stockId;

	protected StockEvent(final Integer stockId) {
		this.stockId = stockId;
	}

	@Override
	public Integer getDomainId() {
		return stockId;
	}
}
