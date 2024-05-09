package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QSellerAccount.*;

import java.util.List;

import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.repository.SellerCustomAccountRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SellerAccountRepositoryImpl implements SellerCustomAccountRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<SellerAccount> findBySellerId(final Integer sellerId) {
		return jpaQueryFactory.selectFrom(sellerAccount)
			.where(sellerAccount.seller.id.eq(sellerId))
			.fetch();
	}
}
