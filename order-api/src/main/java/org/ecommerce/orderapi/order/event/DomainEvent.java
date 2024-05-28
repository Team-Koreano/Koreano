package org.ecommerce.orderapi.order.event;

public interface DomainEvent<T> {
	// TODO : COMMON 이전
	T getDomainId();
}
