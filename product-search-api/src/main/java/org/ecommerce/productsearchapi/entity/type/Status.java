package org.ecommerce.productsearchapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Status implements EnumMapperType {
	/**
	 * AVAILABLE 구매 가능한 상태
	 * OUT_OF_STOCK 재고가 없는 상태
	 * DISCONTINUED 단종, 판매 중단 상태
	 */
	AVAILABLE("구매 가능"),
	OUT_OF_STOCK("재고 없음"),
	DISCONTINUED("판매 중단")
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
