package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum ProcessStatus{
	PENDING("처리 전"),
	IN_PROGRESS("진행 중"),
	FAILED("실패"),
	CANCELLED("취소"),
	COMPLETED("완료");
	private final String title;

	ProcessStatus(String title) {
		this.title = title;
	}
}
