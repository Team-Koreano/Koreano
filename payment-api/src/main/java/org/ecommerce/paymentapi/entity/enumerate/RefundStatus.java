package org.ecommerce.paymentapi.entity.enumerate;

import lombok.Getter;

@Getter
public enum RefundStatus{
	PENDING("환불 처리전"),
	PROCESSING("환불 진행중"),
	COMPLETED("환불 처리완료"),
	FAILED("환불 실패"),
	CANCLLED("환불 취소");

	private final String title;

	RefundStatus(String title) {
		this.title = title;
	}
}
