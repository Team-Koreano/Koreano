package org.ecommerce.orderapi.order.dto.response;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason;

public record OrderItemResponse(
		Long id,
		Integer productId,
		String productName,
		Integer price,
		Integer quantity,
		Integer totalPrice,
		Integer deliveryFee,
		Integer paymentAmount,
		Integer sellerId,
		String sellerName,
		OrderStatus status,
		OrderStatusReason statusReason,
		LocalDateTime statusDatetime,
		LocalDateTime paymentDatetime
) {
}
