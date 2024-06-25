package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QUserBeanPay.*;

import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.repository.UserBeanPayCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserBeanPayRepositoryImpl implements UserBeanPayCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public UserBeanPay findUserBeanPayByUserId(Integer userId) {
		return jpaQueryFactory.selectFrom(userBeanPay)
			.where(userBeanPay.userId.eq(userId))
			.fetchFirst();
	}

	@Override
	public UserBeanPay findUserBeanPayByUserIdUseBetaLock(Integer userId) {
		return jpaQueryFactory.selectFrom(userBeanPay)
			.where(userBeanPay.userId.eq(userId))
			.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.fetchFirst();
	}
}
