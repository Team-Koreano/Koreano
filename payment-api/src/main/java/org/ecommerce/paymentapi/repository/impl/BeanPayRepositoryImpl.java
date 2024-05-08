package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QBeanPay.*;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.repository.BeanPayCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BeanPayRepositoryImpl implements BeanPayCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<BeanPay> findBeanPayByUserIdAndRole(Integer userId, Role role) {

		return Optional.ofNullable(jpaQueryFactory.selectFrom(beanPay)
			.where(
				beanPay.userId.eq(userId)
				.and(
				beanPay.role.eq(role)))
			.fetchOne());
	}

	@Override
	public BeanPay findBeanPayByUserIdAndRoleUseBetaLock(Integer userId, Role role) {
		return jpaQueryFactory.selectFrom(beanPay)
			.where(
				beanPay.userId.eq(userId)
					.and(
				beanPay.role.eq(role)))
			.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.fetchOne();
	}
}
