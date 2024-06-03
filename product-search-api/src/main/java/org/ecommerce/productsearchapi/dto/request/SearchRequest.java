package org.ecommerce.productsearchapi.dto.request;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productsearchapi.enumerated.ProductSortType;
import org.springframework.util.StringUtils;

public record SearchRequest(
	String keyword,
	Boolean isDecaf,
	ProductCategory category,
	Bean bean,
	Acidity acidity,
	ProductSortType sortType
) {

	public boolean validKeyword() {
		return StringUtils.hasText(this.keyword);
	}

	public boolean validIsDecaf() {
		return this.isDecaf != null;
	}

	public boolean validCategory() {
		return this.category != null;
	}

	public boolean validBean() {
		return this.bean != null;
	}

	public boolean validAcidity() {
		return this.acidity != null;
	}

}
