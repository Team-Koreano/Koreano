package org.ecommerce.productsearchapi.enumerated;

import lombok.Getter;

@Getter
public enum ProductSortType {

	NEWEST("createDatetime", "Desc"),
	POPULAR("favoriteCount", "Desc"),
	PRICE_ASC("price", "Asc"),
	PRICE_DESC("price", "Desc"),
	;
	private final String field;
	private final String orderBy;


	ProductSortType(String field, String orderBy) {
		this.field = field;
		this.orderBy = orderBy;
	}

}
