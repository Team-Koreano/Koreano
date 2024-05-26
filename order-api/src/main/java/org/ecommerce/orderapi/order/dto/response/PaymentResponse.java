package org.ecommerce.orderapi.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentResponse(
		Long id,
		Integer totalPaymentAmount,
		LocalDateTime paymentDatetime,
		List<PaymentDetailResponse> paymentDetailResponses
) {
}
