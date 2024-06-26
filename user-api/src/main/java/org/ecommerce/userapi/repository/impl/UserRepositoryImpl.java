package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QUsers.*;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.repository.UserCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber) {
		return jpaQueryFactory
			.selectFrom(users)
			.where(
				users.email.eq(email)
					.or(users.phoneNumber.eq(phoneNumber))
			).fetchFirst() != null;
	}

	@Override
	public Users findUsersByEmailAndIsDeletedIsFalse(final String email) {
		return jpaQueryFactory.selectFrom(users)
			.where(users.email.eq(email)
				, users.isDeleted.isFalse())
			.fetchFirst();
	}

	@Override
	public Users findUsersByIdAndIsDeletedIsFalse(Integer userId) {
		return jpaQueryFactory.selectFrom(users)
			.where(users.id.eq(userId)
				, users.isDeleted.isFalse())
			.fetchFirst();
	}
}
