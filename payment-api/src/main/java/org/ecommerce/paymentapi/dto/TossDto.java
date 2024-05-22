package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.TossErrorMessage.*;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TossDto {
	public static class Request{

		public record TossPayment(
			@NotBlank(message = NOT_BLANK_PAYMENT_TYPE)
			String paymentType,

			@NotBlank(message = NOT_BLANK_PAYMENT_KEY)
			@Size(min = 6, message = PAYMENT_KEY_TOO_SHORT)
			String paymentKey,

			@NotNull(message = NOT_NULL_ORDER_ID)
			UUID orderId,

			@NotNull(message = NOT_NULL_AMOUNT)
			@Min(value = 0, message = NOT_UNDER_ZERO_AMOUNT)
			Integer chargeAmount
		) {
		}
	}

	public record Response() {

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record TossPayment(
			String paymentKey,
			String orderName,
			String method,
			Integer totalAmount,
			@JsonProperty("approvedAt")
			String approveDateTime
		) {}
	}
}
