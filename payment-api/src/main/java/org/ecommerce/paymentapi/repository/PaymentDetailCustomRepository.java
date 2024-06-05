package org.ecommerce.paymentapi.repository;

import java.util.UUID;

import org.ecommerce.paymentapi.entity.PaymentDetail;

public interface PaymentDetailCustomRepository {
	PaymentDetail findPaymentDetailByOrderItemId(Long orderItemId);
	PaymentDetail findPaymentDetailById(UUID id);
}
