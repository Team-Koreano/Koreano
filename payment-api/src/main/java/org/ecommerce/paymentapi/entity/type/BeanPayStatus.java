package org.ecommerce.paymentapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BeanPayStatus implements EnumMapperType {
	DEPOSIT("충전"),
	WITHDRAW("출금");

	private final String title;

	@Override
	public String getCode() {
		return name();
	}

	@Override
	public String getTitle() {
		return this.title;
	}
}
