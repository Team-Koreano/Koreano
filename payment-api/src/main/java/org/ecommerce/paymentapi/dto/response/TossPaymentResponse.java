package org.ecommerce.paymentapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse(
	String paymentKey,
	String orderName,
	String method,
	Integer totalAmount,
	@JsonProperty("approvedAt")
	String approveDateTime
) {}