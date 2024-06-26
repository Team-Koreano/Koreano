package org.ecommerce.userapi.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {
	NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "이메일을 찾을 수 없습니다."),
	NOT_FOUND_ACCOUNT(HttpStatus.BAD_REQUEST, "계좌를 찾을 수 없습니다."),
	NOT_FOUND_ADDRESS(HttpStatus.BAD_REQUEST, "주소를 찾을 수 없습니다"),
	DUPLICATED_EMAIL_OR_PHONENUMBER(HttpStatus.BAD_REQUEST, "중복된 이메일 혹은 전화번호 입니다."),
	IS_NOT_MATCHED_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "이메일 혹은 비밀번호가 일치하지 않습니다."),
	IS_NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	NOT_FOUND_SELLER(HttpStatus.BAD_REQUEST, "셀러를 찾을 수 없습니다."),
	PLEASE_RELOGIN(HttpStatus.BAD_REQUEST, "재로그인 해주시길 바랍니다."),
	IS_NOT_VALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 회원입니다"),
	IS_NOT_VALID_SELLER(HttpStatus.BAD_REQUEST, "유효하지 않은 판매자입니다"),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다");

	UserErrorCode(HttpStatus status, String message) {
		this.message = message;
		this.code = status.value();
	}

	private final String message;
	private final int code;
}
