package org.ecommerce.orderapi.stock.event;

import java.time.LocalDateTime;

public class StockDecreasedEvent extends StockEvent {
	public StockDecreasedEvent(final Integer stockId, final LocalDateTime createdAt) {
		super(stockId, createdAt);
	}
}
