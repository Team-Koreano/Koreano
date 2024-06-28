package org.ecommerce.orderapi.stock.entity.enumerated;

public enum StockOperationResult {
	SUCCESS, SOLD_OUT;

	public boolean isSuccess() {
		return this == SUCCESS;
	}
}
