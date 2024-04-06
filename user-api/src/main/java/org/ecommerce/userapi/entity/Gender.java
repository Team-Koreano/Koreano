package org.ecommerce.userapi.entity;

import lombok.Getter;

@Getter
public enum Gender {
	FEMALE("남자"),
	MALE("여자");

	private final String value;

	Gender(String value) {
		this.value = value;
	}
}
