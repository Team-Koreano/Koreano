package org.ecommerce.paymentapi.repository;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.PaymentDetail;

public interface PaymentDetailCustomRepository {
	Optional<PaymentDetail> findPaymentDetailByOrderItemId(Long orderItemId);
}
