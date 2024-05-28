package org.ecommerce.orderapi.order.dto.request;

import static org.ecommerce.orderapi.order.exception.ErrorMessage.*;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
		@NotNull(message = BUCKET_IDS_NOT_NULL)
		@Size(min = 1, max = 15, message = INVALID_BUCKET_SIZE)
		List<Long> bucketIds,

		@NotBlank(message = RECEIVE_NAME_NOT_BLANK)
		String receiveName,

		@NotBlank(message = PHONE_NUMBER_NOT_BLANK)
		String phoneNumber,

		@NotBlank(message = ADDRESS1_NOT_BLANK)
		String address1,

		@NotBlank(message = ADDRESS2_NOT_BLANK)
		String address2,

		String deliveryComment
) {
}
