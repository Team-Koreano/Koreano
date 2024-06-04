package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QSellerBeanPay.*;

import java.util.List;

import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.repository.SellerBeanPayCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SellerBeanPayRepositoryImpl implements SellerBeanPayCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public SellerBeanPay findSellerBeanPayBySellerId(Integer sellerId) {
		return jpaQueryFactory.selectFrom(sellerBeanPay)
			.where(sellerBeanPay.sellerId.eq(sellerId))
			.fetchFirst();
	}

	@Override
	public List<SellerBeanPay> findSellerBeanPayBySellerIds(List<Integer> sellerIds) {
		return jpaQueryFactory.selectFrom(sellerBeanPay)
			.where(sellerBeanPay.sellerId.in(sellerIds))
			.fetch();
	}
}
