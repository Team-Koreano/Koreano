package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.Payment;

public interface PaymentCustomRepository {
	Payment findByOrderId(Long orderId);
}
