package org.ecommerce.orderapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockOperationType implements EnumMapperType {

	INCREASE("재고 증가"),
	DECREASE("재고 감소")
	;

	private final String title;

	@Override
	public String getCode() {
		return name();
	}
}
