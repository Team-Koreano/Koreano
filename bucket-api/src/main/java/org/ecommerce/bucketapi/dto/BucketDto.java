package org.ecommerce.bucketapi.dto;

import static org.ecommerce.bucketapi.exception.ErrorMessage.*;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BucketDto {

	private Long id;
	private Integer userId;
	private String seller;
	private Integer productId;
	private Integer quantity;
	private LocalDate createDate;

	public static class Request {

		public record Add(

				@NotBlank(message = ERROR_SELLER_REQUIRED)
				String seller,

				@NotNull(message = ERROR_PRODUCT_ID_REQUIRED)
				Integer productId,

				@NotNull(message = ERROR_QUANTITY_REQUIRED)
				@Min(value = 1, message = ERROR_QUANTITY_MIN)
				Integer quantity
		) {

		}

		// TODO : 상품 상세옵션 추가
		public record Modify(

				@NotNull(message = ERROR_QUANTITY_REQUIRED)
				@Min(value = 1, message = ERROR_QUANTITY_MIN)
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

	}
}
