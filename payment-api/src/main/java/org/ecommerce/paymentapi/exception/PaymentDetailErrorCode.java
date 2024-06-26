package org.ecommerce.paymentapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.Getter;

@Getter
public enum PaymentDetailErrorCode implements ErrorCode {
	NOT_FOUND_ID(400, "결제 상세 ID가 존재하지 않습니다."),
	VERIFICATION_FAIL(400, "토스 검증객체가 일치하지 않습니다."),
	TOSS_RESPONSE_FAIL(400, "토스 응답이 실패했습니다."),
	DUPLICATE_API_CALL(400, "중복된 API 요청입니다.");

	PaymentDetailErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private final int code;
	private final String message;

}
