package org.ecommerce.paymentapi.repository;

import java.util.UUID;

import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, UUID>,
	PaymentDetailCustomRepository {

}
