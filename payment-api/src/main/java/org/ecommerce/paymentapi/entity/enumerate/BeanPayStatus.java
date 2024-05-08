package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum BeanPayStatus{
	DEPOSIT("충전"),
	PAYMENT("지불"),
	RECEIVE("수령"),
	WITHDRAW("출금");

	private final String title;

	BeanPayStatus(String title) {
		this.title = title;
	}
}
