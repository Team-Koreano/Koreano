package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason;

public record OrderItemDtoWithOrderStatusHistoryDtoList(
		Long id,
		Integer productId,
		String productName,
		Integer price,
		Integer quantity,
		Integer totalPrice,
		Integer paymentAmount,
		Integer sellerId,
		String sellerName,
		OrderStatus status,
		OrderStatusReason statusReason,
		LocalDateTime statusDatetime,
		List<OrderStatusHistoryDto> orderStatusHistoryDtoList
) {
}
