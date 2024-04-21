package org.ecommerce.common.error;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CustomErrorCode implements ErrorCode{
	INVALID_BUCKET_WITH_USER(601, "요청한 유저와 ID에 해당하는 장바구니가 존재하지 않습니다."),
	NOT_FOUND_BUCKET_ID(602, "존재하지 않는 장바구니 번호 입니다.")
	;

	private final int code;
	private final String message;

	public static CustomErrorCode findByCode(int code) {
		return Arrays.stream(values())
				.filter(error -> error.getCode() == code)
				.findFirst()
				.orElse(null);
	}
}
