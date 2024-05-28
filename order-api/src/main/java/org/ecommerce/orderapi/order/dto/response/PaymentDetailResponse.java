package org.ecommerce.orderapi.order.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDetailResponse(
		UUID id,
		Long orderItemId,
		Integer deliveryFee,
		Integer totalPrice,
		Integer paymentAmount,
		LocalDateTime paymentDatetime
) {
}
