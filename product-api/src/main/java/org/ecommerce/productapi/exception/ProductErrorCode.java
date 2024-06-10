package org.ecommerce.productapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ProductErrorCode implements ErrorCode {
	NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_PRODUCT_DETAIL("상세 상품을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	IS_EQUAL_TO_NOW_STATUS("현재 상품의 상태값과 똑같습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_STATUS("상태값을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	CAN_NOT_BE_SET_TO_BELOW_ZERO("0 보다 적게 설정 할 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_SELLER("판매자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
	IS_INVALID_FILE_OPTION("유효하지않은 확장자 입니다.", HttpStatus.BAD_REQUEST.value()),
	FAILED_FILE_UPLOAD("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_CATEGORY("해당 카테고리를 찾을 수 없습니다", HttpStatus.BAD_REQUEST.value()),
	FAILED_PRODUCT_UPLOAD("상품 등록에 실패하였습니다.", HttpStatus.BAD_REQUEST.value()),
	NOT_FOUND_PRODUCT_ID("존재하지 않는 상품 ID 입니다.", HttpStatus.BAD_REQUEST.value()),
	ONLY_ONE_DEFAULT_PRODUCT_ALLOWED("대표 상품은 한개만 지정 할 수 있습니다", HttpStatus.BAD_REQUEST.value()),
	IS_NOT_ENOUGH_PRODUCT_DETAIL("최소 한개 이상의 상품 상세가 존재해야 합니다.", HttpStatus.BAD_REQUEST.value());

	ProductErrorCode(String message, int code) {
		this.message = message;
		this.code = code;
	}

	private final String message;
	private final int code;
}
