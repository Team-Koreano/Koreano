package org.ecommerce.paymentapi.entity.enumerate;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus implements EnumMapperType {
	PAYMENT("결제"),
	REFUND("환불");

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
