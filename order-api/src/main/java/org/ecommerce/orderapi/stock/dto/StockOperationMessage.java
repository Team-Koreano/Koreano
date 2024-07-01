package org.ecommerce.orderapi.stock.dto;

import lombok.Getter;

@Getter
public class StockOperationMessage {
	private Integer productId;
	private Integer total;

	public static StockOperationMessage of(Integer productId, Integer total) {
		StockOperationMessage stockOperationMessage = new StockOperationMessage();
		stockOperationMessage.productId = productId;
		stockOperationMessage.total = total;
		return stockOperationMessage;
	}
}
