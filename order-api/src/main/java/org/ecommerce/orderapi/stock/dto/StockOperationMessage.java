package org.ecommerce.orderapi.stock.dto;

import lombok.Getter;

@Getter
public class StockOperationMessage {
	private Integer productId;
	private Integer quantity;
}
