package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBeanPayRepository
	extends JpaRepository<UserBeanPay, Integer>, UserBeanPayCustomRepository {
}
