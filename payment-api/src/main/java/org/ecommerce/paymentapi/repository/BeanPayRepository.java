package org.ecommerce.paymentapi.repository;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface BeanPayRepository extends JpaRepository<BeanPay, Integer> {
	Optional<BeanPay> findBeanPayByUserIdAndRole(Integer userId, Role role);

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("select b from BeanPay b where b.userId =:userId and b.role =:role")
	BeanPay findBeanPayByUserIdAndRoleUseBetaLock(Integer userId, Role role);
}
