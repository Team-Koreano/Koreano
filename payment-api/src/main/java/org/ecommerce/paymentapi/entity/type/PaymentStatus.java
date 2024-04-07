package org.ecommerce.paymentapi.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
	PAYMENT("결제"),
	REFUND("환불");

	private final String description;
}
