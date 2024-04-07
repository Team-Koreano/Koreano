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
		@Builder
		public record GetProductDto(
			Integer id,
			Boolean is_decaf,
			Integer price,
			Integer sellerId,
			Integer stock,
			String acidity,
			String bean,
			String category,
			String information,
			String name,
			String status
		){}
	}
}
