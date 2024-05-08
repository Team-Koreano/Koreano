package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PaymentDto {
	private Integer id;
	private Integer orderId;
	private Integer userId;
	private Integer sellerId;
	private Integer totalAmount;
	private String orderName;
	private ProcessStatus processStatus;
	private LocalDateTime createDateTime;
	private Boolean isVisible;


	public static class Request {
		public record PaymentPrice(
			@Min(value = 1, message = "1이상의 주문ID를 전달해주세요")
			Long orderId,
			@Min(value = 0, message = "0이상의 총 금액을 전달해주세요")
			Integer totalAmount,
			@Min(value = 1, message = "1이상의 유저ID를 전달해주세요")
			Integer userId,
			@Min(value = 1, message = "1이상의 셀러ID를 전달해주세요")
			Integer sellerId,
			@NotBlank(message = "주문명을 입력해주세요")
			String orderName,
			List<PaymentDetailPrice> paymentDetails
		) {
		}

		public record PaymentRollBack(
			@Min(value = 1, message = "1이상의 주문ID를 전달해주세요")
			Long orderId,
			@Min(value = 1, message = "1이상의 유저ID를 전달해주세요")
			Integer userId,
			@Min(value = 1, message = "1이상의 셀러ID를 전달해주세요")
			Integer sellerId
		) {
		}
	}

	public record Response() {
	}
}
