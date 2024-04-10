package org.ecommerce.bucketapi.exception;

import org.ecommerce.common.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BucketErrorCode implements ErrorCode {
	;
	private final int code;
	private final String message;
}
