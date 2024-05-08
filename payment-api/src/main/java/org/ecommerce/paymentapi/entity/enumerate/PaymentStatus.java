package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum PaymentStatus{
	PAYMENT("결제"),
	ROLLBACK("롤백"),
	REFUND("환불");

	private final String title;

	PaymentStatus(String title) {
		this.title = title;
	}
}
