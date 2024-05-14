package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QSeller.*;

import java.util.Optional;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.repository.SellerCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public boolean existsByEmailOrPhoneNumber(String email, String phoneNumber) {
		return jpaQueryFactory
			.selectFrom(seller)
			.where(
				emailEq(email)
					.or(phoneNumberEq(phoneNumber))
			).fetchFirst() != null;
	}

	@Override
	public Optional<Seller> findSellerByEmailAndIsDeletedIsFalse(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(seller)
				.where(emailEq(email),
					seller.isDeleted.eq(false))
				.fetchFirst()
		);
	}

	@Override
	public Optional<Seller> findSellerByIdAndIsDeletedIsFalse(Integer sellerId) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(seller)
				.where(idEq(sellerId),
					seller.isDeleted.eq(false))
				.fetchFirst()
		);
	}

	private BooleanExpression emailEq(final String email) {
		return email != null ? seller.email.eq(email) : null;
	}

	private BooleanExpression idEq(final Integer sellerId) {
		return sellerId != null ? seller.id.eq(sellerId) : null;
	}

	private BooleanExpression phoneNumberEq(final String phoneNumber) {
		return phoneNumber != null ? seller.password.eq(phoneNumber) : null;
	}
}
