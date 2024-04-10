package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.product.entity.Product;

public class ProductManagementDto {

	public record Response(
		Integer id,
		Boolean isDecaf,
		Integer price,
		String bizName,
		Integer stock,
		Integer favoriteCount,
		String acidity,
		String bean,
		String category,
		String information,
		String name,
		String status,
		LocalDateTime createDatetime
	) {
		public static Response of(final Product product) {
			return new Response(
				product.getId(),
				product.getIsDecaf(),
				product.getPrice(),
				product.getSellerRep().getBizName(),
				product.getStock(),
				product.getFavoriteCount(),
				product.getAcidity().getTitle(),
				product.getBean().getTitle(),
				product.getCategory().getTitle(),
				product.getInformation(),
				product.getName(),
				product.getStatus().getTitle(),
				product.getCreateDatetime()
			);
		}
	}
}