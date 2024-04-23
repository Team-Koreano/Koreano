package org.ecommerce.paymentapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BeanPayErrorCode implements ErrorCode {
	NOT_FOUND_ID(400, "빈페이의 ID를 찾을 수 없습니다.");

	private final int code;
	private final String message;
}
