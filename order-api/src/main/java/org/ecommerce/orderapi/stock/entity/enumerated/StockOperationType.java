package org.ecommerce.orderapi.stock.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum StockOperationType implements EnumMapperType {

	INCREASE("재고 증가"),
	DECREASE("재고 감소");

	private final String title;

	StockOperationType(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
