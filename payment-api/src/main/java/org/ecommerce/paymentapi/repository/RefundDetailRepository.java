package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.RefundDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundDetailRepository extends JpaRepository<RefundDetail, Long> {
}
