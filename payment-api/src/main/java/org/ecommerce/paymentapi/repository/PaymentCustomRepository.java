package org.ecommerce.paymentapi.repository;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.Payment;

public interface PaymentCustomRepository {
	Optional<Payment> findByOrderId(Long orderId);
}
