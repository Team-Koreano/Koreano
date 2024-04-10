package org.ecommerce.product.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Acidity implements EnumMapperType {
	LIGHT("라이트"),
	CINNAMON("시나몬"),
	MEDIUM("미디움"),
	HIGH("하이"),
	CITY("시티"),
	FULL_CITY("풀 시티"),
	FRENCH("프렌치"),
	ITALIAN("이탈리안")
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
