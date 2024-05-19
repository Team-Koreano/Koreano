package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

import lombok.Getter;

public abstract class OrderEvent implements DomainEvent<Long> {
	// TODO : 보내줄 정보를 정해야 함 (Payload or Id)
	private final Long orderId;
	@Getter
	private final LocalDateTime createdAt;

	protected OrderEvent(final Long orderId, final LocalDateTime createdAt) {
		this.orderId = orderId;
		this.createdAt = createdAt;
	}

	@Override
	public Long getDomainId() {
		return orderId;
	}
}
