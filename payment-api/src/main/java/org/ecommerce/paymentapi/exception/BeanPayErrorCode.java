package org.ecommerce.paymentapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BeanPayErrorCode implements ErrorCode {
	NOT_EXIST(400, "빈페이 ID가 존재하지 않습니다."),
	VERIFICATION_FAIL(400, "토스 검증객체가 일치하지 않습니다."),
	TOSS_RESPONSE_FAIL(400, "토스 응답이 실패했습니다.");

	private final int code;
	private final String message;

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
