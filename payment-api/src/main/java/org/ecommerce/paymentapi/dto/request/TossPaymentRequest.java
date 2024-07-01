package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.TossErrorMessage.*;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TossPaymentRequest(
	@NotBlank(message = NOT_BLANK_PAYMENT_TYPE)
	String paymentType,

	@NotBlank(message = NOT_BLANK_PAYMENT_KEY)
	@Size(min = 6, message = PAYMENT_KEY_TOO_SHORT)
	String paymentKey,

	@NotNull(message = NOT_NULL_ORDER_ID)
	UUID orderId,

	@NotNull(message = NOT_NULL_AMOUNT)
	@Min(value = 0, message = NOT_UNDER_ZERO_AMOUNT)
	Integer amount,

	@NotNull(message = NOT_NULL_USER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
	Integer userId
) {
}