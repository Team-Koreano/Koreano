package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerBeanPayRepository
	extends JpaRepository<SellerBeanPay, Integer>, SellerBeanPayCustomRepository {
}
