package org.ecommerce.paymentapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ProcessStatus implements EnumMapperType {
	PENDING("처리 전"),
	IN_PROGRESS("진행 중"),
	FAILED("실패"),
	CANCELLED("취소"),
	COMPLETED("완료");
	private final String title;

	ProcessStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
