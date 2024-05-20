package org.ecommerce.orderapi.order.dto;

import org.ecommerce.orderapi.order.entity.enumerated.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductDto {
	private Integer id;
	private String name;
	private Integer price;
	private Integer sellerId;
	private String sellerName;
	private ProductStatus status;

	public record Response(
			Integer id,
			String name,
			Integer price,
			Integer sellerId,
			String sellerName,
			ProductStatus status
	) {
	}
}
