package org.ecommerce.paymentapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum PaymentStatus implements EnumMapperType {
	PAYMENT("결제"),
	REFUND("환불");

	private final String title;

	PaymentStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
