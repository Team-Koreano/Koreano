package org.ecommerce.paymentapi.repository.impl;

import static org.ecommerce.paymentapi.entity.QPaymentDetail.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.repository.PaymentDetailCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PaymentDetailRepositoryImpl implements PaymentDetailCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PaymentDetail findPaymentDetailByOrderItemId(Long orderItemId) {
		return jpaQueryFactory.selectFrom(paymentDetail)
				.where(paymentDetail.orderItemId.eq(orderItemId))
				.leftJoin(paymentDetail.userBeanPay).fetchJoin()
				.leftJoin(paymentDetail.sellerBeanPay).fetchJoin()
				.fetchFirst();
	}

	public PaymentDetail findPaymentDetailById(UUID id) {
		return jpaQueryFactory.selectFrom(paymentDetail)
				.where(paymentDetail.id.eq(id))
				.leftJoin(paymentDetail.userBeanPay).fetchJoin()
				.fetchFirst();
	}

	@Override
	public List<PaymentDetail> findByUserIdAndBetweenCreateDateTime(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status,
		Pageable pageable
	) {
		return jpaQueryFactory.selectFrom(
				paymentDetail)
			.where(
				paymentDetail.userBeanPay.userId.eq(userId),
				getStatus(status),
				paymentDetail.createDateTime.between(start, end),
				paymentDetail.isVisible.eq(true))
			.leftJoin(paymentDetail.chargeInfo).fetchJoin()
			.leftJoin(paymentDetail.sellerBeanPay).fetchJoin()
			.leftJoin(paymentDetail.userBeanPay).fetchJoin()
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(paymentDetail.createDateTime.desc())
			.fetch();


	}
	@Override
	public long userPaymentDetailCountByUserIdAndBetweenCreateDateTime(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status
	) {
		return jpaQueryFactory.selectFrom(paymentDetail)
			.where(
				paymentDetail.userBeanPay.userId.eq(userId),
				getStatus(status),
				paymentDetail.createDateTime.between(start, end),
				paymentDetail.isVisible.eq(true))
			.fetchCount();
	}

	@Override
	public List<PaymentDetail> findBySellerIdAndBetweenCreateDateTime(Integer sellerId,
		LocalDateTime start, LocalDateTime end, PaymentStatus status, Pageable pageable) {
		return jpaQueryFactory.selectFrom(paymentDetail)
			.where(
				paymentDetail.sellerBeanPay.sellerId.eq(sellerId),
				getStatus(status),
				paymentDetail.createDateTime.between(start, end))
			.leftJoin(paymentDetail.userBeanPay).fetchJoin()
			.leftJoin(paymentDetail.sellerBeanPay).fetchJoin()
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(paymentDetail.createDateTime.desc())
			.fetch();
	}

	@Override
	public long sellerPaymentDetailCountByUserIdAndBetweenCreatedDateTime(Integer sellerId,
		LocalDateTime start, LocalDateTime end, PaymentStatus status) {
		return jpaQueryFactory.selectFrom(paymentDetail)
			.where(
				paymentDetail.sellerBeanPay.sellerId.eq(sellerId),
				getStatus(status),
				paymentDetail.createDateTime.between(start, end))
			.fetchCount();
	}

	private static BooleanExpression getStatus(PaymentStatus status) {
		return status != null ?
			paymentDetail.paymentStatus.eq(status) :
			paymentDetail.paymentStatus.isNotNull();
	}
}
