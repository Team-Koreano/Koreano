package org.ecommerce.paymentapi.exception;

import lombok.Getter;

@Getter
public class TossErrorMessage {
	public static final String paymentTypeBlank = "결제타입을 입력해주세요";
	public static final String paymentKeyBlank = "결제키를 입력해주세요";
	public static final String paymentKeySize = "결제키 사이즈가 6자 이상이여야 합니다";
	public static final String orderIdBlank = "충전 ID를 입력해주세요";
	public static final String amountMinMessage = "0원 이상을 입력해주세요";
}
