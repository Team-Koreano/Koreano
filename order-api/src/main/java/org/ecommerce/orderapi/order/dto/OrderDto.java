package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

public record OrderDto(
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
		LocalDateTime orderDatetime
) {
}
