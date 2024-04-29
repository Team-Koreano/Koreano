package org.ecommerce.orderapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StockErrorCode implements ErrorCode {
	STOCK_SERIALIZATION_FAIL(HttpStatus.BAD_REQUEST.value(), "재고 직렬화 실패"),
	STOCK_DESERIALIZATION_FAIL(HttpStatus.BAD_REQUEST.value(), "재고 역직렬화 실패");
	private final int code;
	private final String message;
}
