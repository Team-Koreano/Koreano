package org.ecommerce.orderapi.event;

public interface DomainEvent<T> {
	// TODO : COMMON 이전
	T getDomainId();
}
