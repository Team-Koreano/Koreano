package org.ecommerce.bucketapi.dto;

import java.time.LocalDate;

import org.ecommerce.bucketapi.entity.Bucket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BucketDto {

	public static class Request {

		public record Add(

			@NotBlank(message = "판매자를 입력해 주세요.")
			String seller,

			@NotNull(message = "상품 번호를 입력해 주세요.")
			Integer productId,

			@NotNull(message = "상품 수량을 입력해 주세요.")
			@Min(value = 1, message = "상품 수량을 1개 이상으로 입력해 주세요.")
			Integer quantity
		) {

		}

		// TODO : 상품 상세옵션 추가
		public record Update(

			@NotNull(message = "상품 수량을 입력해 주세요.")
			@Min(value = 1, message = "상품 수량을 1개 이상으로 입력해 주세요.")
			Integer quantity
		) {

		}
	}

	public record Response(
		Long id,
		Integer userId,
		String seller,
		Integer productId,
		Integer quantity,
		LocalDate createDate
	) {
		public static Response of(final Bucket bucket) {
			return new Response(
				bucket.getId(),
				bucket.getUserId(),
				bucket.getSeller(),
				bucket.getProductId(),
				bucket.getQuantity(),
				bucket.getCreateDate()
			);
		}
	}
}
