package org.ecommerce.orderapi.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDetailDto {
	private UUID id;
	private Long orderItemId;
	private Integer deliveryFee;
	private Integer totalPrice;
	private Integer paymentAmount;
	private LocalDateTime paymentDatetime;

	public record Response(
			UUID id,
			Long orderItemId,
			Integer deliveryFee,
			Integer totalPrice,
			Integer paymentAmount,
			LocalDateTime paymentDatetime
	) {
	}
}
