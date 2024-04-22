package org.ecommerce.paymentapi.repository;

import java.util.UUID;

import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeanPayDetailRepository extends JpaRepository<BeanPayDetail, UUID> {
}
