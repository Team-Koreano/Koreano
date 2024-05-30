package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDetailDto(
		UUID id,
		Long orderItemId,
		Integer deliveryFee,
		Integer totalPrice,
		Integer paymentAmount,
		LocalDateTime paymentDatetime
) {
}
