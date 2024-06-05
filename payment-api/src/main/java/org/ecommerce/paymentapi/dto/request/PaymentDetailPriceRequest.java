package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentDetailPriceRequest(
	@JsonProperty("id")
	@NotNull(message = NOT_NULL_ORDER_ITEM_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ITEM_ID)
	Long orderItemId,
	@NotNull(message = NOT_NULL_PRICE)
	@Min(value = 0, message = NOT_UNDER_ZERO_PRICE)
	Integer price,
	@NotNull(message = NOT_NULL_QUANTITY)
	@Min(value = 0, message = NOT_UNDER_ZERO_QUANTITY)
	Integer quantity,
	@NotNull(message = NOT_NULL_DELIVERY_FEE)
	@Min(value = 0, message = NOT_UNDER_ZERO_DELIVERY_FEE)
	Integer deliveryFee,
	@NotNull(message = NOT_NULL_SELLER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
	Integer sellerId,
	@NotBlank(message = NOT_BLANK_PRODUCT_NAME)
	String productName
) {
}
