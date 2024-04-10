package org.ecommerce.bucketapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BucketErrorCode implements ErrorCode {
	;
	private final int code;
	private final String message;

	@Override
	public int getCode() {
		return this.code;
	}
	@Override
	public String getMessage() {
		return this.message;
	}
}
