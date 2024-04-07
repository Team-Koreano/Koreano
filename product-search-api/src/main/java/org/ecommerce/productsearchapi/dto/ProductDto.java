package org.ecommerce.productsearchapi.dto;

import lombok.Builder;

public class ProductDto {
	public static class Request {
		@Builder
		public record CreateProductDto(
			String category,
			Integer price,
			Integer stock,
			Integer sellerId,
			Boolean isDecaf,
			String name,
			String bean,
			String acidity,
			String status,
			String information
		){}
	}
	public static class Response {
	}
}
