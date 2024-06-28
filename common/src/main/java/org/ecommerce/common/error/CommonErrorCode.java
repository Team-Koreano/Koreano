package org.ecommerce.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {
	EXPIRED_JWT(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
	INVALID_SIGNATURE_JWT(HttpStatus.BAD_REQUEST, "토큰의 형식을 만족하지 않습니다."),
	EMPTY_JWT(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
	UNRELIABLE_JWT(HttpStatus.BAD_REQUEST, "신뢰할 수 없는 토큰입니다."),
	INVALID_AUTHORIZATION(HttpStatus.BAD_REQUEST, "권한이 없습니다."),
	AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "인증에 실패하였습니다.");

	CommonErrorCode(HttpStatus status, String message) {
		this.message = message;
		this.code = status.value();
	}

	private final String message;
	private final int code;
}
