package org.ecommerce.userapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum UserStatus implements EnumMapperType {

	GENERAL("일반 회원"),
	WITHDRAWAL("탈퇴 회원"),
	SUSPEND("정지 회원");

	private final String title;

	UserStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}

}
