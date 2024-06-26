package org.ecommerce.paymentapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum BeanPayErrorCode implements ErrorCode {
	NOT_FOUND_ID(HttpStatus.BAD_REQUEST.value(), "빈페이의 ID를 찾을 수 없습니다."),
	NOT_FOUND_SELLER_ID(HttpStatus.BAD_REQUEST.value(), "판매자 ID를 찾을 수 없습니다." ),
	INSUFFICIENT_AMOUNT(HttpStatus.BAD_REQUEST.value(), "잔액이 부족합니다"),
	ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "유저가 이미 존재합니다."),
	ALREADY_DELETE(HttpStatus.BAD_REQUEST.value(), "이미 임시 삭제 처리 되었습니다."),
	REMAIN_BEANPAY(HttpStatus.BAD_REQUEST.value(), "잔액이 남아있어 삭제할 수 없습니다.");

	BeanPayErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private final int code;
	private final String message;
}
