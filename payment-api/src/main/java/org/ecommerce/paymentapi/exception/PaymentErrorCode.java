package org.ecommerce.paymentapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum PaymentErrorCode implements ErrorCode {
	NOT_FOUND_ORDER_ID(HttpStatus.BAD_REQUEST.value(), "주문 ID를 찾을 수 없습니다");

	private final int code;
	private final String message;

	PaymentErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
