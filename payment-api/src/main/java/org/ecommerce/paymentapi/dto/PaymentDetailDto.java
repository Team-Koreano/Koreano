package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.PaymentDetailErrorMessage.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDetailDto {
	private UUID id;
	private Long paymentId;
	private Integer userId;
	private Integer sellerId;
	private Long orderItemId;
	private Integer deliveryFee;
	private Integer paymentAmount;
	private Integer quantity;
	private String paymentName;
	private String cancelReason;
	private String failReason;
	private String paymentKey;
	private String payType;
	private PaymentStatus paymentStatus;
	private ProcessStatus processStatus;
	private LocalDateTime approveDateTime;
	private LocalDateTime createDateTime;
	private LocalDateTime updateDateTime;


	public static class Request {

		public record PreCharge(
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@Min(value = 0, message = NOT_UNDER_ZERO_CHARGE_AMOUNT)
			Integer amount
		) {
		}

		public record TossFail(
			@NotNull(message = NOT_BLANK_CHARGE_ID)
			UUID orderId,
			@NotBlank(message = NOT_BLANK_ERROR_CODE)
			String errorCode,
			@NotBlank(message = NOT_BLANK_ERROR_MESSAGE)
			String errorMessage
		) {
		}

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
			@NotBlank(message = NOT_BLANK_PRODUCT_NAME)
			String productName
		) {
		}
	}

	public record Response() {
	}
}
