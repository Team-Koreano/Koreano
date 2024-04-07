package org.ecommerce.paymentapi.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundStatus {
	PENDING("환불 처리전"),
	PROCESSING("환불 진행중"),
	COMPLETED("환불 처리완료"),
	FAILED("환불 실패"),
	CANCLLED("환불 취소");

	private final String description;
}
