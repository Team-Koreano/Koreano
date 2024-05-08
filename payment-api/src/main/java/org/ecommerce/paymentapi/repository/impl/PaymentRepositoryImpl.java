package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QPayment.*;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.QPayment;
import org.ecommerce.paymentapi.repository.PaymentCustomRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Payment> findByOrderId(Long orderId) {
		return Optional.ofNullable(jpaQueryFactory.selectFrom(payment)
			.where(payment.orderId.eq(orderId))
			.leftJoin(payment.userBeanPayDetail).fetchJoin()
			.leftJoin(payment.sellerBeanPayDetail).fetchJoin()
			.leftJoin(payment.paymentDetails).fetchJoin()
			.fetchOne()
		);
	}
}
