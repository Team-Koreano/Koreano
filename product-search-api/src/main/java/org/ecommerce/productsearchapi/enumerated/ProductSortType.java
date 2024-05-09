package org.ecommerce.productsearchapi.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum ProductSortType implements EnumMapperType {

	NEWEST("createDatetime"),
	POPULAR("favoriteCount"),
	;
	private final String title;

	ProductSortType(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
