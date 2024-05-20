package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QPaymentDetail.*;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.repository.PaymentDetailCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PaymentDetailRepositoryImpl implements PaymentDetailCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<PaymentDetail> findPaymentDetailByOrderItemId(Long orderItemId) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(paymentDetail)
			.where(paymentDetail.orderItemId.eq(orderItemId))
				.join(paymentDetail.userBeanPay).fetchJoin()
				.join(paymentDetail.sellerBeanPay).fetchJoin()
			.fetchFirst()
		);
	}
}
