package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QSeller.*;

import java.util.Optional;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.repository.SellerCustomRepository;
import org.springframework.stereotype.Repository;

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
				seller.email.eq(email)
					.or(seller.phoneNumber.eq(phoneNumber))
			).fetchFirst() != null;
	}

	@Override
	public Optional<Seller> findSellerByEmailAndIsDeletedIsFalse(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(seller)
				.where(seller.email.eq(email),
					seller.isDeleted.eq(false))
				.fetchFirst()
		);
	}

	@Override
	public Optional<Seller> findSellerByIdAndIsDeletedIsFalse(Integer sellerId) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(seller)
				.where(seller.id.eq(sellerId),
					seller.isDeleted.eq(false))
				.fetchFirst()
		);
	}
}
