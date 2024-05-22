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
	private Long paymentDetailId;
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
			@NotNull(message = NOT_NULL_USER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotNull(message = NOT_NULL_CHARGE_AMOUNT)
			@Min(value = 0, message = NOT_UNDER_ZERO_CHARGE_AMOUNT)
			Integer chargeAmount
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

			@NotNull(message = NOT_NULL_ORDER_ITEM_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ITEM_ID)
			Long orderItemId,
			@NotNull(message = NOT_NULL_PAYMENT_AMOUNT)
			@Min(value = 0, message = NOT_UNDER_ZERO_PAYMENT_AMOUNT)
			Integer paymentAmount,
			@NotNull(message = NOT_NULL_PRICE)
			@Min(value = 0, message = NOT_UNDER_ZERO_PRICE)
			Integer price,
			@NotNull(message = NOT_NULL_QUANTITY)
			@Min(value = 0, message = NOT_UNDER_ZERO_QUANTITY)
			Integer quantity,
			@NotNull(message = NOT_NULL_DELIVERY_FEE)
			@Min(value = 0, message = NOT_UNDER_ZERO_DELIVERY_FEE)
			Integer deliveryFee,
			@NotNull(message = NOT_NULL_SELLER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
			Integer sellerId,
			@NotBlank(message = NOT_BLANK_PRODUCT_NAME)
			String productName
		) {
		}

		public record PaymentCancel(
			@NotNull(message = NOT_NULL_USER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotNull(message = NOT_NULL_SELLER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_SELLER_ID)
			Integer sellerId,
			@NotNull(message = NOT_NULL_ORDER_ITEM_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_ORDER_ITEM_ID)
			Long orderItemId,
			@NotBlank(message = NOT_BLANK_CANCEL_REASON)
			String cancelReason
		) {
		}
	}

	public record Response(
		UUID id,
		Long paymentDetailId,
		Integer userId,
		Integer sellerId,
		Long orderItemId,
		Integer deliveryFee,
		Integer paymentAmount,
		Integer quantity,
		String paymentName,
		String cancelReason,
		String failReason,
		String paymentKey,
		String payType,
		PaymentStatus paymentStatus,
		ProcessStatus processStatus,
		LocalDateTime approveDateTime,
		LocalDateTime createDateTime,
		LocalDateTime updateDateTime
	) {
	}
}
