package org.ecommerce.userapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender implements EnumMapperType {

	MALE("남성"),
	FEMALE("여성");

	private final String title;

	@Override
	public String getCode() {
		return name();
	}

}
