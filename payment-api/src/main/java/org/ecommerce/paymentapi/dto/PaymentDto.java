package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.PaymentErrorMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDto {
	private Long id;
	private Long orderId;
	private Integer userId;
	private Integer totalAmount;
	private String orderName;
	private List<PaymentDetailDto> paymentDetails;
	private ProcessStatus processStatus;
	private LocalDateTime createDateTime;
	private Boolean isVisible;


	public static class Request {
		public record PaymentPrice(
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
			Long orderId,
			@Min(value = 0, message = NOT_UNDER_ZERO_AMOUNT)
			Integer totalAmount,
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotBlank(message = NOT_BLANK_ORDER_NAME)
			String orderName,
			List<PaymentDetailPrice> paymentDetails
		) {
			public List<Integer> extractSellerIds() {
				return paymentDetails.stream()
					.map(PaymentDetailPrice::sellerId)
					.toList();
			}
		}

		public record PaymentRollBack(
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
			Long orderId,
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
			Integer sellerId
		) {
		}
	}

	public record Response(
		 Long id,
		 Long orderId,
		 Integer userId,
		 Integer totalAmount,
		 String orderName,
		 List<PaymentDetailDto.Response> paymentDetails,
		 ProcessStatus processStatus,
		 LocalDateTime createDateTime,
		 Boolean isVisible
	) {
	}
}
