package org.ecommerce.paymentapi.repository;

import java.util.List;
import java.util.Optional;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;

public interface BeanPayCustomRepository {
	Optional<BeanPay> findBeanPayByUserIdAndRole(Integer userId, Role role);
	BeanPay findBeanPayByUserIdAndRoleUseBetaLock(Integer userId, Role role);
	List<BeanPay> findBeanPayByUserIdsAndRole(List<Integer> userIds, Role role);
}
