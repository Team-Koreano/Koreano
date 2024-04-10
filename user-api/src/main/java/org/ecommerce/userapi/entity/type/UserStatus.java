package org.ecommerce.userapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus implements EnumMapperType {

	GENERAL("일반 회원"),
	WITHDRAWAL("탈퇴 회원"),
	SUSPEND("정지 회원");

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
