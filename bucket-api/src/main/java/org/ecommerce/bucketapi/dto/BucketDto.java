package org.ecommerce.bucketapi.dto;

import java.time.LocalDate;

import org.ecommerce.bucketapi.entity.Bucket;

public class BucketDto {

	public static class Request {

	}

	public record Response(
		Long id,
		Integer userId,
		Integer productId,
		Integer quantity,
		LocalDate createDate
	) {
		public static Response of(final Bucket bucket) {
			return new Response(
				bucket.getId(),
				bucket.getUserId(),
				bucket.getProductId(),
				bucket.getQuantity(),
				bucket.getCreateDate()
			);
		}
	}
}
