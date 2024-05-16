package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum LockName {
	BEANPAY("빈페이");

	private final String title;

	LockName(String title){
		this.title = title;
	}
}
