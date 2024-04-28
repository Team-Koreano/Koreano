package org.ecommerce.orderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Stock {

	private Integer productId;

	private Integer total;
	private Integer processingCnt;

	public Integer getAvailableStock() {
		return this.total - this.processingCnt;
	}

	public void increaseProcessingCnt(final Integer quantity) {
		this.processingCnt += quantity;
	}
}
