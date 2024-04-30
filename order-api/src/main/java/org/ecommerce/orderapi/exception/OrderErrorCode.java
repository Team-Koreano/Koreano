package org.ecommerce.orderapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderErrorCode implements ErrorCode {
	NOT_FOUND_PRODUCT_ID(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 상품 번호입니다."),
	INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST.value(), "가용한 재고가 부족합니다."),
	INSUFFICIENT_STOCK_INFORMATION(HttpStatus.BAD_REQUEST.value(), "재고 정보가 부족합니다.");
	private final int code;
	private final String message;
}