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
	public Optional<Users> findUsersByEmailAndPhoneNumber(final String email, final String phoneNumber) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(users)
				.where(emailEq(email)
					.and(phoneNumberEq(phoneNumber)))
				.fetchFirst()
		);
	}

	@Override
	public boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber) {
		return jpaQueryFactory.select(users.count())
			.from(users)
			.where(
				emailEq(email)
					.or(phoneNumberEq(phoneNumber))
			)
			.fetchFirst() > 0;
	}

	@Override
	public Optional<Users> findUsersByEmail(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(users)
				.where(emailEq(email))
				.fetchFirst()
		);
	}

	private BooleanExpression emailEq(final String email) {
		return email != null ? users.email.eq(email) : null;
	}

	private BooleanExpression phoneNumberEq(final String phoneNumber) {
		return phoneNumber != null ? users.phoneNumber.eq(phoneNumber) : null;
	}

}
