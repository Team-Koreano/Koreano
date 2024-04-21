package org.ecommerce.productsearchapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductSearchErrorCode implements ErrorCode {

	NOT_FOUND_PRODUCT_ID(400, "존재하지 않는 상품 ID 입니다.");

	private final int code;
	private final String message;

}
