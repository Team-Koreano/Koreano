package org.ecommerce.orderapi.bucket.dto.request;

import static org.ecommerce.orderapi.bucket.exception.ErrorMessage.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifyBucketRequest(
		@NotNull(message = ERROR_QUANTITY_REQUIRED)
		@Min(value = 1, message = ERROR_QUANTITY_MIN)
		Integer quantity
) {
}
