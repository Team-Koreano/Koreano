package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.PaymentErrorMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDto {
	private Long id;
	private Long orderId;
	private Integer userId;
	private Integer totalPaymentAmount;
	private String orderName;
	private List<PaymentDetailDto> paymentDetailDtos;
	private ProcessStatus processStatus;
	private LocalDateTime createDateTime;
	private Boolean isVisible;


	public static class Request {

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record PaymentPrice(
			@JsonProperty("id")
			@NotNull(message = NOT_NULL_ORDER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
			Long orderId,
			@NotNull(message = NOT_NULL_USER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotBlank(message = NOT_BLANK_ORDER_NAME)
			String orderName,
			@JsonProperty("orderItemDtos")
			List<PaymentDetailPrice> paymentDetails
		) {
			public List<Integer> extractSellerIds() {
				return paymentDetails.stream()
					.map(PaymentDetailPrice::sellerId)
					.toList();
			}
		}

		public record PaymentRollBack(
			@NotNull(message = NOT_NULL_ORDER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ID)
			Long orderId,
			@NotNull(message = NOT_NULL_USER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotNull(message = NOT_NULL_SELLER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
			Integer sellerId
		) {
		}
	}

	public record Response(
		Long id,
		Long orderId,
		Integer userId,
		Integer totalPaymentAmount,
		String orderName,
		List<PaymentDetailDto.Response> paymentDetailResponses,
		ProcessStatus processStatus,
		LocalDateTime createDateTime,
		Boolean isVisible
	) {
	}
}
