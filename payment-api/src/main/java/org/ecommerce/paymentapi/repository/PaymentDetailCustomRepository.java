package org.ecommerce.paymentapi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;

public interface PaymentDetailCustomRepository {
	PaymentDetail findPaymentDetailByOrderItemId(Long orderItemId);
	PaymentDetail findPaymentDetailById(UUID id);
	List<PaymentDetail> findByUserIdAndBetweenCreateDateTime(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status,
		Integer page,
		Integer size
	);
	long userPaymentDetailCountByUserIdAndBetweenCreateDateTime(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status
	);

	List<PaymentDetail> findBySellerIdAndBetweenCreateDateTime(
		Integer sellerId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status,
		Pageable pageable
	);
	long sellerPaymentDetailCountByUserIdAndBetweenCreatedDateTime(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status
	);
}
