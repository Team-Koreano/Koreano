package org.ecommerce.orderapi.stock.exception;

import org.ecommerce.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StockErrorCode implements ErrorCode {
	STOCK_TRANSACTION_LOCK(HttpStatus.BAD_REQUEST.value(), "해당 재고는 사용 중입니다."),
	MUST_CANCELLED_ORDER_TO_INCREASE_STOCK(HttpStatus.BAD_REQUEST.value(),
			"재고를 증가하려면 주문 상태가 CANCELLED 여야 합니다."),
	MUST_REFUND_REASON_TO_INCREASE_STOCK(HttpStatus.BAD_REQUEST.value(),
			"재고를 증가하려면 사유가 REFUND 여야 합니다."),
	MUST_DECREASE_STOCK_OPERATION_TYPE_TO_INCREASE_STOCK(HttpStatus.BAD_REQUEST.value(),
			"재고를 감소하려면 이전 재고 작업 타입이 DECREASE 여야 합니다."),
	MUST_SUCCESS_OPERATION_RESULT_TO_INCREASE_STOCK(HttpStatus.BAD_REQUEST.value(),
			"재고를 감소하려면 이전 재고 작업 타입이 성공해야 합니다."),
	MUST_ORDER_ITEM_STATUS_APPROVE_TO_DECREASE_STOCK(HttpStatus.BAD_REQUEST.value(),
			"재고를 감소하려면 주문항목 상태가 APPROVE 여야 합니다."),
	NOT_FOUND_STOCK(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 재고입니다."),
	NOT_FOUND_STOCK_HISTORY(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 재고 이력입니다.");

	private final int code;
	private final String message;
}
