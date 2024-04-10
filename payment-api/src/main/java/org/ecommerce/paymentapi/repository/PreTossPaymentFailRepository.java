package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.PreTossPaymentFail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreTossPaymentFailRepository extends JpaRepository<PreTossPaymentFail, Long> {
}
