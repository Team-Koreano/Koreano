package org.ecommerce.orderapi.event;

public abstract class OrderEvent implements DomainEvent<Long> {
	// TODO : 보내줄 정보를 정해야 함 (Payload or Id)
	private final Long orderId;

	protected OrderEvent(final Long orderId) {
		this.orderId = orderId;
	}

	@Override
	public Long getDomainId() {
		return orderId;
	}
}
