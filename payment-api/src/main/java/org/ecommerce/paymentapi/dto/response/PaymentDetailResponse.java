package org.ecommerce.paymentapi.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

public record PaymentDetailResponse(
	UUID id,
	Long paymentDetailId,
	Integer userId,
	Integer sellerId,
	Long orderItemId,
	Integer deliveryFee,
	Integer totalAmount,
	Integer paymentAmount,
	Integer price,
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