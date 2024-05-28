package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

public record OrderStatusHistoryDto(
		Long id,
		OrderStatus changeStatus,
		LocalDateTime statusChangeDatetime
) {
}
