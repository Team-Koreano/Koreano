package org.ecommerce.orderapi.stock.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StockDto {
	private Integer id;
	private Integer productId;
	private Integer total;
	private LocalDateTime createDatetime;

	public record Response(
			Integer id,
			Integer productId,
			Integer total,
			LocalDateTime createDatetime
	) {
	}
}
