package org.ecommerce.userapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus implements EnumMapperType {


	GENERAL("일반_회원"),
	WITHDRAWAL("탈퇴_회원"),
	SUSPEND("정지_회원")
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
