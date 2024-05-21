package org.ecommerce.orderapi.stock.event;

public class StockIncreasedEvent extends StockEvent {
	public StockIncreasedEvent(final Integer stockId) {
		super(stockId);
	}
}
