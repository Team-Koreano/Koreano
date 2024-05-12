package org.ecommerce.orderapi.event;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.entity.Order;

import lombok.Getter;

@Getter
public abstract class OrderEvent implements DomainEvent<Order> {
	// TODO : 보내줄 정보를 정해야 함 (Payload or Id)
	private final OrderDto orderDto;
	private final LocalDateTime createdAt;

	protected OrderEvent(final OrderDto orderDto, final LocalDateTime createdAt) {
		this.orderDto = orderDto;
		this.createdAt = createdAt;
	}

}
