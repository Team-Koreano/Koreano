package org.ecommerce.orderapi.order.event;

public abstract class OrderEvent implements DomainEvent<Long> {
	private final Long orderId;

	protected OrderEvent(final Long orderId) {
		this.orderId = orderId;
	}

	@Override
	public Long getDomainId() {
		return orderId;
	}
}
