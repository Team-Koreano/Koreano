package org.ecommerce.orderapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderErrorCode implements ErrorCode {
	NOT_FOUND_PRODUCT_ID(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 상품 번호입니다."),
	NOT_FOUND_ORDER_DETAIL_ID(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 주문 상세 번호입니다."),
	MUST_CLOSED_ORDER_TO_CANCEL(HttpStatus.BAD_REQUEST.value(), "주문을 취소하려면 주문 상태가 CLOSED 여야 합니다."),
	TOO_OLD_ORDER_TO_CANCEL(HttpStatus.BAD_REQUEST.value(), "일주일이 지난 주문은 취소가 불가능합니다."),
	INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST.value(), "가용한 재고가 부족합니다."),
	INSUFFICIENT_STOCK_INFORMATION(HttpStatus.BAD_REQUEST.value(), "재고 정보가 부족합니다."),
	NOT_AVAILABLE_PRODUCT(HttpStatus.BAD_REQUEST.value(), "판매하지 않는 상품입니다."),
	NOT_FOUND_ORDER_DETAIL(HttpStatus.BAD_REQUEST.value(), "상세 주문이 존재하지 않습니다.");

	private final int code;
	private final String message;
}
