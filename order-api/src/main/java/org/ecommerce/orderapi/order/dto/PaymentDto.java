package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record PaymentDto(
		Long id,
		Integer totalPaymentAmount,
		LocalDateTime paymentDatetime,
		Map<Long, PaymentDetailDto> paymentDetailDtoMap
) {
}
