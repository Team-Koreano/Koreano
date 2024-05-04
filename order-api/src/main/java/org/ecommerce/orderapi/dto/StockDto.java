package org.ecommerce.orderapi.dto;

import java.time.LocalDate;

public class StockDto {
	private Integer id;
	private Integer productId;
	private Integer total;
	private LocalDate createDatetime;

	public record Response(
			Integer id,
			Integer productId,
			Integer total,
			LocalDate createDatetime
	) {
	}
}
