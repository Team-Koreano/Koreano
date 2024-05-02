package org.ecommerce.paymentapi.entity.enumerate;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProcessStatus implements EnumMapperType {
	PENDING("처리 전"),
	IN_PROGRESS("진행 중"),
	FAILED("실패"),
	CANCELLED("취소"),
	COMPLETED("완료");
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
