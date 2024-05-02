package org.ecommerce.userapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum Gender implements EnumMapperType {

	MALE("남성"),
	FEMALE("여성");

	private final String title;

	Gender(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}

}
