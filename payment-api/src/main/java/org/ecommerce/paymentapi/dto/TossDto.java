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
			@NotBlank(message = paymentTypeBlank)
			String paymentType,

			@NotBlank(message = paymentKeyBlank)
			@Size(min = 6, message = paymentKeySize)
			String paymentKey,

			@NotNull(message = orderIdBlank)
			UUID orderId,

			@Min(value = 0, message = amountMinMessage)
			Integer amount
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
