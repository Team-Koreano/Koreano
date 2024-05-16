package org.ecommerce.paymentapi.exception;

public class PaymentDetailErrorMessage {
	public static final String NOT_UNDER_ONE_ORDER_DETAIL_ID = "1이상의 주문상세ID를 전달해주세요";
	public static final String NOT_UNDER_ZERO_TOTAL_AMOUNT = "0이상의 총액을 전달해주세요";
	public static final String NOT_UNDER_ZERO_PAYMENT_AMOUNT = "0이상의 결제액을 전달해주세요";
	public static final String NOT_UNDER_ZERO_PRICE = "0이상의 상품금액을 전달해주세요";
	public static final String NOT_UNDER_ZERO_QUANTITY = "0이상의 수량을 전달해주세요";
	public static final String NOT_UNDER_ZERO_DELIVERY_FEE = "0이상의 배달료를 전달해주세요";
	public static final String NOT_UNDER_ONE_SELLER_ID = "1이상의 판매자ID를 전달해주세요";
	public static final String BLANK_PRODUCT_NAME = "제품의 이름을 전달해주세요";
}
