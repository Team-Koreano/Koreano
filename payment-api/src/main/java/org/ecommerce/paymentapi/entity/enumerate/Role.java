package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum Role{

	USER("일반 유저"),
	SELLER("판매 유저");

	private final String title;

	Role(String title) {
		this.title = title;
	}
}
