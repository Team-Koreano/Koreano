package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusHistoryDto {
	private Long id;
	private OrderStatus changeStatus;
	private LocalDateTime statusChangeDatetime;

	public record Response(
			Long id,
			OrderStatus changeStatus,
			LocalDateTime statusChangeDatetime
	) {
	}
}
