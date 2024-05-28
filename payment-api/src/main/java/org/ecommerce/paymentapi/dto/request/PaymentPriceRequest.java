package org.ecommerce.paymentapi.dto.request;

import static org.ecommerce.paymentapi.exception.PaymentErrorMessage.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentPriceRequest(
	@JsonProperty("id")
	@NotNull(message = NOT_NULL_ORDER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
	Long orderId,
	@NotNull(message = NOT_NULL_USER_ID)
	@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
	Integer userId,
	@NotBlank(message = NOT_BLANK_ORDER_NAME)
	String orderName,
	@JsonProperty("orderItemDtos")
	List<PaymentDetailPriceRequest> paymentDetails
) {
	public List<Integer> extractSellerIds() {
		return paymentDetails.stream()
			.map(PaymentDetailPriceRequest::sellerId)
			.toList();
	}
}