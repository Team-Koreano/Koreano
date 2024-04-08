package org.ecommerce.orderapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatusReason implements EnumMapperType {

	REFUND("환불"),
	FAILED_PAYMENT("결제실패"),
	EXCHANGE("교환"),
	SIMPLE_CHANGE_OF_HEART("단순변심")
	;

	private final String title;

	@Override
	public String getCode() {
		return name();
	}

	@Override
	public String getTitle() {
		return title;
	}
}
