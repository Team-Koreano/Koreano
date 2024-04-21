package org.ecommerce.orderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductDto {
	private Integer id;
	private Integer price;
	private Integer stock;
	private String seller;

	public record Response(
			Integer id,
			Integer price,
			Integer stock,
			String seller
	) {
	}
}
