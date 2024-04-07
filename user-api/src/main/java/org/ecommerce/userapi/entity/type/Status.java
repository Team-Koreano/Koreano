package org.ecommerce.userapi.entity.type;

import lombok.Getter;

@Getter
public enum Status {

	GENERAL("일반"),
	WITHDRAWAL("탈퇴"),
	SUSPEND("정지");

	private final String value;

	Status(String value) {
		this.value = value;
	}
}
