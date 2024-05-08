package org.ecommerce.paymentapi.dto;

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
			@Min(value = 1, message = "1이상의 주문상세ID를 전달해주세요")
			Integer orderDetailId,
			@Min(value = 0, message = "0이상의 총액을 전달해주세요")
			Integer totalPrice,
			@Min(value = 0, message = "0이상의 결제액을 전달해주세요")
			Integer paymentAmount,
			@Min(value = 0, message = "0이상의 상품금액을 전달해주세요")
			Integer price,
			@Min(value = 0, message = "0이상의 수량을 전달해주세요")
			Integer quantity,
			@Min(value = 0, message = "0이상의 배달료를 전달해주세요")
			Integer deliveryFee,
			@Min(value = 1, message = "1이상의 판매자ID를 전달해주세요")
			Integer sellerId,
			@NotBlank(message = "제품의 이름을 전달해주세요")
			String productName
		) {
		}
	}

	public record Response() {
	}
}
