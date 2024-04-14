package org.ecommerce.bucketapi.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BucketDto {

	private Long id;
	private Integer userId;
	private String seller;
	private Integer productId;
	private Integer quantity;
	private LocalDate createDate;

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
	}

	public record Response(
		Long id,
		Integer userId,
		String seller,
		Integer productId,
		Integer quantity,
		LocalDate createDate
	) {
		public static Response of(final BucketDto bucketDto) {
			return new Response(
				bucketDto.id,
				bucketDto.userId,
				bucketDto.seller,
				bucketDto.productId,
				bucketDto.quantity,
				bucketDto.createDate
			);
		}
	}
}
