package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentCancelRequest(
	@NotNull(message = NOT_NULL_USER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
	Integer userId,
	@NotNull(message = NOT_NULL_SELLER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
	Integer sellerId,
	@NotNull(message = NOT_NULL_ORDER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
	Long orderId,
	@NotNull(message = NOT_NULL_ORDER_ITEM_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ITEM_ID)
	Long orderItemId,
	@NotBlank(message = NOT_BLANK_CANCEL_REASON)
	String cancelReason
) {
}