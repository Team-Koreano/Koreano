package org.ecommerce.orderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Stock {

	private Integer productId;

	private Integer total;

	public void decreaseTotalStock(Integer quantity) {
		this.total -= quantity;
	}

	public boolean hasStock(Integer quantity) {
		return this.total >= quantity;
	}

	public boolean isSoldOut() {
		return this.total == 0;
	}
}
