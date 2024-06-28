package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.BeanPayErrorMessage.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSellerBeanPayRequest(
	@NotNull(message = NOT_NULL_SELLER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
	Integer sellerId
) {
}