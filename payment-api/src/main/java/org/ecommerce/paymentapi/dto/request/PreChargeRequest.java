package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PreChargeRequest(
	@NotNull(message = NOT_NULL_CHARGE_AMOUNT)
	@Min(value = 0, message = NOT_UNDER_ZERO_CHARGE_AMOUNT)
	Integer chargeAmount
) {
}