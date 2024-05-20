package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

import lombok.Getter;

public abstract class StockEvent implements DomainEvent<Integer> {
	// TODO : 보내줄 정보를 정해야 함 (Payload or Id)
	private final Integer stockId;
	@Getter
	private final LocalDateTime createdAt;

	protected StockEvent(final Integer stockId, final LocalDateTime createdAt) {
		this.stockId = stockId;
		this.createdAt = createdAt;
	}

	@Override
	public Integer getDomainId() {
		return stockId;
	}
}
