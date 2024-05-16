package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QUsers.*;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.repository.UserCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
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
				emailEq(email)
					.or(phoneNumberEq(phoneNumber))
			).fetchFirst() != null;
	}

	@Override
	public Optional<Users> findUsersByEmailAndIsDeletedIsFalse(final String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(users)
				.where(emailEq(email)
					, users.isDeleted.isFalse())
				.fetchFirst()
		);
	}

	@Override
	public Optional<Users> findUsersByIdAndIsDeletedIsFalse(Integer userId) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(users)
				.where(idEq(userId)
					, users.isDeleted.isFalse())
				.fetchFirst()
		);
	}

	private BooleanExpression emailEq(final String email) {
		return email != null ? users.email.eq(email) : null;
	}

	private BooleanExpression phoneNumberEq(final String phoneNumber) {
		return phoneNumber != null ? users.phoneNumber.eq(phoneNumber) : null;
	}

	private BooleanExpression idEq(final Integer userId) {
		return userId != null ? users.id.eq(userId) : null;
	}
}
