package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PaymentDetailDto {
	private Integer id;
	private Integer userId;
	private Integer sellerId;
	private Integer totalAmount;
	private String orderName;
	private LocalDateTime createDateTime;
	private LocalDateTime updateDateTime;


	public static class Request {
		public record PaymentDetailPrice(
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_DETAIL_ID)
			Long orderDetailId,
			@Min(value = 0, message = NOT_UNDER_ZERO_TOTAL_AMOUNT)
			Integer totalPrice,
			@Min(value = 0, message = NOT_UNDER_ZERO_PAYMENT_AMOUNT)
			Integer paymentAmount,
			@Min(value = 0, message = NOT_UNDER_ZERO_PRICE)
			Integer price,
			@Min(value = 0, message = NOT_UNDER_ZERO_QUANTITY)
			Integer quantity,
			@Min(value = 0, message = NOT_UNDER_ZERO_DELIVERY_FEE)
			Integer deliveryFee,
			@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
			Integer sellerId,
			@NotBlank(message = BLANK_PRODUCT_NAME)
			String productName
		) {
		}
	}

	public record Response() {
	}
}
