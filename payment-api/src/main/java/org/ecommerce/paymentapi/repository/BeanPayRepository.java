package org.ecommerce.paymentapi.repository;

import java.util.UUID;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeanPayRepository extends JpaRepository<BeanPay, UUID> {
}
