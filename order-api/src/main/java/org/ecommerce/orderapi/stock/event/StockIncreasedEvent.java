package org.ecommerce.orderapi.stock.event;

import java.time.LocalDateTime;

public class StockIncreasedEvent extends StockEvent {
	public StockIncreasedEvent(final Integer stockId, final LocalDateTime createdAt) {
		super(stockId, createdAt);
	}
}
