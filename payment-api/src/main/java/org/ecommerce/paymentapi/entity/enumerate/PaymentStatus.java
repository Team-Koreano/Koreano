package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum PaymentStatus{
	DEPOSIT("충전"),
	PAYMENT("결제"),
	RECEIVE("수령"),
	WITHDRAW("출금"),
	REFUND("환불");

	private final String title;

	PaymentStatus(String title) {
		this.title = title;
	}
}
