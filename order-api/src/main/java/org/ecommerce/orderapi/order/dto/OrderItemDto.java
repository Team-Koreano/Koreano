package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemDto {

	private Long id;
	private Integer productId;
	private String productName;
	private Integer price;
	private Integer quantity;
	private Integer totalPrice;
	private Integer paymentAmount;
	private Integer sellerId;
	private String sellerName;
	private OrderStatus status;
	private OrderStatusReason statusReason;
	private LocalDateTime statusDatetime;

	public record Response(
			Long id,
			Integer productId,
			String productName,
			Integer price,
			Integer quantity,
			Integer totalPrice,
			Integer paymentAmount,
			Integer sellerId,
			String sellerName,
			OrderStatus status,
			OrderStatusReason statusReason,
			LocalDateTime statusDatetime
	) {
	}
}
