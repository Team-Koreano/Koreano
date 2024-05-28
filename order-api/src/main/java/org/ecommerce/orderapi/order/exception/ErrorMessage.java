package org.ecommerce.orderapi.order.exception;

public class ErrorMessage {
	public static final String BUCKET_IDS_NOT_NULL = "주문할 장바구니를 입력해 주세요.";
	public static final String RECEIVE_NAME_NOT_BLANK = "수신자 이름을 입력해 주세요.";
	public static final String PHONE_NUMBER_NOT_BLANK = "휴대폰 번호를 입력해 주세요.";
	public static final String ADDRESS1_NOT_BLANK = "도로명 주소를 입력해 주세요.";
	public static final String ADDRESS2_NOT_BLANK = "상세주소를 입력해 주세요.";
	public static final String INVALID_BUCKET_SIZE = "장바구니 크기는 1 이상 15 이하 입니다.";
}
