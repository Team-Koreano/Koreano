package org.ecommerce.orderapi.order.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum OrderStatus implements EnumMapperType {

	OPEN("주문요청"),
	APPROVE("주문승인"),
	CLOSED("주문완료"),
	CANCELLED("주문취소")
	;

	private final String title;

	OrderStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
