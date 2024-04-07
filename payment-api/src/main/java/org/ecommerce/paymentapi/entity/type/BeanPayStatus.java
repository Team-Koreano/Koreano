package org.ecommerce.paymentapi.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BeanPayStatus {
	DEPOSIT("충전"),
	WITHDRAW("출금");

	private final String description;
}
