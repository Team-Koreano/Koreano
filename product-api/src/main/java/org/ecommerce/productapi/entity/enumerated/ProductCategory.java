package org.ecommerce.productapi.entity.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum ProductCategory implements EnumMapperType {
	/**
	 * BEAN 원두
	 * CUP 커피 잔
	 * CUP_STAND 컵 받침
	 * BLENDER 블렌더
	 * MACHINE 커피머신
	 */
	BEAN("원두"),
	CUP("커피 잔"),
	CUP_STAND("컵 받침"),
	BLENDER("블렌더"),
	MACHINE("커피머신");
	private final String title;

	ProductCategory(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
