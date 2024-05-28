package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossFailRequest(
	@NotNull(message = NOT_BLANK_CHARGE_ID)
	UUID orderId,
	@NotBlank(message = NOT_BLANK_ERROR_CODE)
	String errorCode,
	@NotBlank(message = NOT_BLANK_ERROR_MESSAGE)
	String errorMessage
) {
}