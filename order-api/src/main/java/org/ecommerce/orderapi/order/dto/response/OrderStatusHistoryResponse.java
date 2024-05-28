package org.ecommerce.orderapi.order.dto.response;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

public record OrderStatusHistoryResponse(
		Long id,
		OrderStatus changeStatus,
		LocalDateTime statusChangeDatetime
) {
}
