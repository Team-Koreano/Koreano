package org.ecommerce.orderapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum OrderStatusReason implements EnumMapperType {

	REFUND("환불"),
	FAILED_PAYMENT("결제실패"),
	EXCHANGE("교환"),
	SIMPLE_CHANGE_OF_HEART("단순변심")
	;

	private final String title;

	OrderStatusReason(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
