package org.ecommerce.orderapi.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BucketDto {
	private Long id;
	private Integer userId;
	private String seller;
	private Integer productId;
	private Integer quantity;
	private LocalDate createDate;

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
