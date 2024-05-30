package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QPayment.*;
import static org.ecommerce.paymentapi.entity.QPaymentDetail.*;

import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.repository.PaymentCustomRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Payment findByOrderId(Long orderId) {
		return jpaQueryFactory.selectFrom(payment)
			.where(payment.orderId.eq(orderId))
			.leftJoin(payment.paymentDetails, paymentDetail).fetchJoin()
			.leftJoin(payment.userBeanPay).fetchJoin()
			.leftJoin(paymentDetail.sellerBeanPay).fetchJoin()
			.fetchFirst();
	}
}
