package org.ecommerce.userapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"중복된 이메일 입니다"),
	DUPLICATED_PHONENUMBER(HttpStatus.BAD_REQUEST,"중복된 전화번호 입니다")
	;

	UserErrorCode(HttpStatus status, String message) {
		this.message = message;
		this.status = status.value();
	}

	private final String message;
	private final int status;

	@Override
	public int getCode() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
