package org.ecommerce.productapi.enumerated;

import lombok.Getter;

@Getter
public enum ProductDocumentField {
	ID("id"),
	CATEGORY("category"),
	PRICE("price"),
	STOCK("stock"),
	SELLER_ID("sellerId"),
	SELLER_NAME("sellerName"),
	FAVORITE_COUNT("favoriteCount"),
	IS_DECAF("isDecaf"),
	NAME("name"),
	ACIDITY("acidity"),
	BEAN("bean"),
	INFORMATION("information"),
	THUMBNAIL_URL("thumbnailUrl"),
	;

	private final String field;

	ProductDocumentField(String field) {
		this.field = field;
	}

}
