package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.enumerated.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductDto {
	private Integer id;

	private String name;
	private Integer price;
	private String seller;
	private ProductStatus status;

	public record Response(
			Integer id,
			String name,
			Integer price,
			String seller,
			ProductStatus status
	) {
	}
}
