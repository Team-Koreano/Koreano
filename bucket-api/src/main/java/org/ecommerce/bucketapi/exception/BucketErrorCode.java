package org.ecommerce.bucketapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BucketErrorCode implements ErrorCode {
	INVALID_BUCKET_WITH_USER(400, "요청한 유저와 ID에 해당하는 장바구니가 존재하지 않습니다."),
	NOT_FOUND_BUCKET_ID(400, "존재하지 않는 장바구니 번호 입니다.");
	private final int code;
	private final String message;
}
