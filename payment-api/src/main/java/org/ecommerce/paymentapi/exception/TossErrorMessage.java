package org.ecommerce.paymentapi.exception;

import lombok.Getter;

@Getter
public class TossErrorMessage {
	public static final String paymentTypeBlank = "결제타입을 입력해주세요";
	public static final String paymentKeyBlank = "결제키을 입력해주세요";
	public static final String paymentKeySize = "결제키을 입력해주세요";
	public static final String orderIdBlank = "결제키을 입력해주세요";
	public static final String amountMinMessage = "결제키을 입력해주세요";
}
