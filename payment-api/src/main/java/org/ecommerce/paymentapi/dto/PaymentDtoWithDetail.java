package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

public record PaymentDtoWithDetail(
	 Long id,
	 Long orderId,
	 Integer userId,
	 Integer totalPaymentAmount,
	 String orderName,
	 List<PaymentDetailDto> paymentDetailDtos,
	 ProcessStatus processStatus,
	 LocalDateTime createDateTime,
	 Boolean isVisible
) {
}
