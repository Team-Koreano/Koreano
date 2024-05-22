package org.ecommerce.paymentapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.PaymentDetail;

public interface PaymentDetailCustomRepository {
	Optional<PaymentDetail> findPaymentDetailByOrderItemId(Long orderItemId);
	Optional<PaymentDetail> findPaymentDetailById(UUID id);
}
