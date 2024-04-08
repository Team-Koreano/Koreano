package org.ecommerce.orderapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus implements EnumMapperType {

	OPEN("주문요청"),
	ACCEPTED("주문승인"),
	CLOSED("배송완료"),
	CANCELLED("주문취소")
	;

	private final String title;

	@Override
	public String getCode() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}
}
