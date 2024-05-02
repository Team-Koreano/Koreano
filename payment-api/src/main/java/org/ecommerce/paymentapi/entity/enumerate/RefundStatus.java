package org.ecommerce.paymentapi.entity.enumerate;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RefundStatus implements EnumMapperType {
	PENDING("환불 처리전"),
	PROCESSING("환불 진행중"),
	COMPLETED("환불 처리완료"),
	FAILED("환불 실패"),
	CANCLLED("환불 취소");

	private final String title;

	@Override
	public String getCode() {
		return name();
	}
	@Override
	public String getTitle() {
		return this.title;
	}
}
