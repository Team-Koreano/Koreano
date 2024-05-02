package org.ecommerce.paymentapi.entity.type;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum RefundStatus implements EnumMapperType {
	PENDING("환불 처리전"),
	PROCESSING("환불 진행중"),
	COMPLETED("환불 처리완료"),
	FAILED("환불 실패"),
	CANCLLED("환불 취소");

	private final String title;

	RefundStatus(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}
}
