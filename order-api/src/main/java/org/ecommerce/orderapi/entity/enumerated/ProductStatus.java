package org.ecommerce.orderapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductStatus implements EnumMapperType {

	AVAILABLE("구매 가능"),
	OUT_OF_STOCK("재고 없음"),
	DISCONTINUED("판매 중단");
	private final String title;

	@Override
	public String getCode() {
		return name();
	}
}
