package org.ecommerce.paymentapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

public record PaymentWithDetailResponse(
	Long id,
	Long orderId,
	Integer userId,
	Integer totalPaymentAmount,
	String orderName,
	List<PaymentDetailResponse> paymentDetailResponses,
	ProcessStatus processStatus,
	LocalDateTime createDateTime,
	Boolean isVisible
) {
}