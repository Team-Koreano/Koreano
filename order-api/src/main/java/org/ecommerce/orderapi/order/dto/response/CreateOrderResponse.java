package org.ecommerce.orderapi.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

public record CreateOrderResponse(
		Long id,
		Integer userId,
		String userName,
		String receiveName,
		String phoneNumber,
		String address1,
		String address2,
		String deliveryComment,
		Integer totalPaymentAmount,
		OrderStatus status,
		LocalDateTime statusDatetime,
		LocalDateTime paymentDatetime,
		LocalDateTime orderDatetime,
		List<OrderItemResponse> orderItemResponses
) {
}
