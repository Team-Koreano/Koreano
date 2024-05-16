package org.ecommerce.productsearchapi.enumerated;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.Getter;

@Getter
public enum ProductDocumentField implements EnumMapperType {
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

	private final String title;

	ProductDocumentField(String title) {
		this.title = title;
	}

	@Override
	public String getCode() {
		return name();
	}

}
