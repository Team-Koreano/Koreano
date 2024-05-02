package org.ecommerce.userapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum Role implements EnumMapperType {

	USER("일반 유저"),
	SELLER("판매 유저");

	private final String title;

	Role(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
