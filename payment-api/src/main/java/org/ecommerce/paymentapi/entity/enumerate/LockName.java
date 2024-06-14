package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum LockName {
	USER_BEANPAY("유저빈페이"),
	SELLER_BEANPAY("판매자빈페이");

	private final String title;

	LockName(String title){
		this.title = title;
	}
}
