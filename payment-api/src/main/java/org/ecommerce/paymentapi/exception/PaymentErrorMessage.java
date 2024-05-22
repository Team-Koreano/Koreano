package org.ecommerce.paymentapi.exception;

public class PaymentErrorMessage {
	public static final String NOT_UNDER_ONE_ORDER_ID = "1이상의 주문ID를 전달해주세요";
	public static final String NOT_UNDER_ZERO_AMOUNT = "0이상의 총 금액을 전달해주세요";
	public static final String NOT_UNDER_ONE_USER_ID = "1이상의 유저ID를 전달해주세요";
	public static final String NOT_UNDER_ONE_SELLER_ID = "1이상의 셀러ID를 전달해주세요";
	public static final String NOT_BLANK_ORDER_NAME = "주문명을 입력해주세요";
	public static final String NOT_NULL_ORDER_ID = "주문ID를 전달해주세요";
	public static final String NOT_NULL_TOTAL_AMOUNT = "주문 금액을 전달해주세요";
	public static final String NOT_NULL_USER_ID = "유저ID를 전달해주세요";
	public static final String NOT_NULL_SELLER_ID = "판매자ID를 전달해주세요";
}
