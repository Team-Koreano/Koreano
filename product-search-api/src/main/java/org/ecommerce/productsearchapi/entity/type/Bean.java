package org.ecommerce.productsearchapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Bean implements EnumMapperType {
	ARABICA("아라비카"),
	ROBUSTA("로부스타"),
	LIBERICA("리베리카"),
	EXCELSA("엑셀사")
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
