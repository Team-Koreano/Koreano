package org.ecommerce.product.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
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

	@Override
	public String getCode() {
		return name();
	}
}
