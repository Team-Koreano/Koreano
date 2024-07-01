package org.ecommerce.paymentapi.exception;

import lombok.Getter;

@Getter
public class TossErrorMessage {
	public static final String NOT_BLANK_PAYMENT_TYPE = "결제타입을 입력해주세요";
	public static final String NOT_BLANK_PAYMENT_KEY = "결제키를 입력해주세요";
	public static final String PAYMENT_KEY_TOO_SHORT = "결제키 사이즈가 6자 이상이여야 합니다";
	public static final String NOT_NULL_ORDER_ID = "충전 ID를 입력해주세요";
	public static final String NOT_UNDER_ZERO_AMOUNT = "0원 이상을 입력해주세요";
	public static final String NOT_NULL_AMOUNT = "금액을 입력해주세요";
	public static final String NOT_NULL_USER_ID = "유저ID를 전달해주세요";
	public static final String NOT_UNDER_ONE_USER_ID = "1이상의 유저ID를 전달해주세요";
}