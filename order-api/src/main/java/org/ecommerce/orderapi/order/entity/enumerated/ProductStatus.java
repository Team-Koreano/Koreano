package org.ecommerce.orderapi.order.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ProductStatus implements EnumMapperType {

	AVAILABLE("구매 가능"),
	OUT_OF_STOCK("재고 없음"),
	DISCONTINUED("판매 중단");
	private final String title;

	ProductStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
