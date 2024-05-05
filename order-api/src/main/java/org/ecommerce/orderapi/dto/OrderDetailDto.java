package org.ecommerce.orderapi.dto;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.entity.enumerated.OrderStatusReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDetailDto {

	private Long id;
	private Integer productId;
	private Integer price;
	private Integer quantity;
	private Integer totalPrice;
	private Integer paymentAmount;
	private String seller;
	private OrderStatus status;
	private OrderStatusReason statusReason;
	private List<OrderStatusHistory> orderStatusHistories;

	public record Response(
			Long id,
			Integer productId,
			Integer price,
			Integer quantity,
			Integer totalPrice,
			Integer paymentAmount,
			String seller,
			OrderStatus status,
			OrderStatusReason statusReason,
			List<OrderStatusHistory> orderStatusHistories
	) {
	}
}
