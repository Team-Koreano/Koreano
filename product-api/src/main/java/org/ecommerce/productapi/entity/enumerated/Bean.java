package org.ecommerce.productapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum Bean implements EnumMapperType {
	ARABICA("아라비카"),
	ROBUSTA("로부스타"),
	LIBERICA("리베리카"),
	EXCELSA("엑셀사"),
	NONE("NONE");

	private final String title;

	Bean(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
