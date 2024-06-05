package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

public record PaymentDto(
	Long id,
	Long orderId,
	Integer userId,
	Integer totalPaymentAmount,
	String orderName,
	ProcessStatus processStatus,
	LocalDateTime createDateTime,
	Boolean isVisible
) {
}
