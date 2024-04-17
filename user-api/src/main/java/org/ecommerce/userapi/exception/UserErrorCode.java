package org.ecommerce.userapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"중복된 이메일 입니다"),
	DUPLICATED_PHONENUMBER(HttpStatus.BAD_REQUEST,"중복된 전화번호 입니다"),
	NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST,"이메일을 찾을 수 없습니다"),
	IS_NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다"),
	EXPIRED_JWT(HttpStatus.BAD_REQUEST,"만료된 토큰입니다"),
	INVALID_SIGNATURE_JWT(HttpStatus.BAD_REQUEST, "토큰의 형식을 만족하지 않습니다."),
	EMPTY_JWT(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
	UNRELIABLE_JWT(HttpStatus.BAD_REQUEST, "신뢰할 수 없는 토큰입니다."),
	RELOGIN(HttpStatus.BAD_REQUEST, "다시 로그인 해주세요"),
	INVALID_AUTHORIZATION(HttpStatus.BAD_REQUEST,"권한이 없습니다"),
	AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST,"인증에 실패하였습니다")
	;

	UserErrorCode(HttpStatus status, String message) {
		this.message = message;
		this.code = status.value();
	}

	private final String message;
	private final int code;
}
