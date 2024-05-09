package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QBeanPay.*;
import static org.ecommerce.paymentapi.entity.QBeanPayDetail.*;
import static org.ecommerce.paymentapi.entity.QPayment.*;
import static org.ecommerce.paymentapi.entity.QPaymentDetail.*;

import java.util.Optional;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.QBeanPay;
import org.ecommerce.paymentapi.entity.QBeanPayDetail;
import org.ecommerce.paymentapi.entity.QPayment;
import org.ecommerce.paymentapi.entity.QPaymentDetail;
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
			.leftJoin(payment.userBeanPay).fetchJoin()
			.leftJoin(payment.paymentDetails).fetchJoin()
			.leftJoin(paymentDetail.sellerBeanPayDetail).fetchJoin()
			.leftJoin(paymentDetail.userBeanPayDetail).fetchJoin()
			.leftJoin(beanPayDetail.beanPay).fetchJoin()
			.fetchOne()
		);
	}
}
