package org.ecommerce.product.entity.enumerated;

import java.util.stream.Stream;

import org.ecommerce.common.utils.mapper.EnumMapperType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ProductStatus implements EnumMapperType {
	/**
	 * AVAILABLE 구매 가능한 상태
	 * OUT_OF_STOCK 재고가 없는 상태
	 * DISCONTINUED 단종, 판매 중단 상태
	 */
	AVAILABLE("구매 가능"),
	OUT_OF_STOCK("재고 없음"),
	DISCONTINUED("판매 중단");
	private final String title;

	ProductStatus(String title) {
		this.title = title;
	}

	public static ProductStatus findByCode(String name) {
		return Stream.of(ProductStatus.values())
			.filter(c -> c.name().equals(name.replaceAll("-","_").toUpperCase()))
			.findFirst()
			.orElse(ProductStatus.DISCONTINUED);
	}

	@Override
	public String getCode() {
		return name();
	}
}
