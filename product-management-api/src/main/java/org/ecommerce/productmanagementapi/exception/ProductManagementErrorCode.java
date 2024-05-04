package org.ecommerce.productmanagementapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ProductManagementErrorCode implements ErrorCode {
	NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	IS_EQUAL_TO_NOW_STATUS("현재 상품의 상태값과 똑같습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_STATUS("상태값을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	CAN_NOT_BE_SET_TO_BELOW_ZERO("0 보다 적게 설정 할 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_SELLER("판매자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	IS_INVALID_FILE_OPTION("유효하지않은 확장자 입니다.", HttpStatus.BAD_REQUEST.value());

	ProductManagementErrorCode(String message, int code) {
		this.message = message;
		this.code = code;
	}

	private final String message;
	private final int code;
}
