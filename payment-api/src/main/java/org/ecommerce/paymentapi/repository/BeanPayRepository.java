package org.ecommerce.paymentapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.userapi.entity.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeanPayRepository extends JpaRepository<BeanPay, UUID> {
	Optional<BeanPay> findBeanPayByUserIdAndRole(Integer userId, Role role);
}
