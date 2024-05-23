package org.ecommerce.orderapi.order.repository.impl;

import static org.ecommerce.orderapi.order.entity.QOrder.*;
import static org.ecommerce.orderapi.order.entity.QOrderItem.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.repository.OrderItemCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderItemRepositoryImpl implements OrderItemCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<OrderItem> findOrderItemById(final Long orderItemId) {
		return Optional.ofNullable(
				jpaQueryFactory.selectFrom(orderItem)
						.leftJoin(orderItem.orderStatusHistories).fetchJoin()
						.where(orderItem.id.eq(orderItemId))
						.fetchFirst()
		);
	}

	@Override
	public Page<OrderItem> findOrderItemsBySellerIdAndMonth(
			final Integer sellerId,
			final Integer month,
			final Pageable pageable
	) {
		List<OrderItem> content = jpaQueryFactory
				.selectFrom(orderItem)
				.leftJoin(orderItem.order, order).fetchJoin()
				.where(orderItem.sellerId.eq(sellerId),
						generateDateCondition(month))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> countQuery = jpaQueryFactory
				.select(orderItem.count())
				.from(orderItem)
				.leftJoin(orderItem.order, order)
				.where(orderItem.sellerId.eq(sellerId),
						generateDateCondition(month));
		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression generateDateCondition(final Integer month) {
		LocalDateTime start;
		LocalDateTime end;
		LocalDateTime now = LocalDateTime.now();

		if (month == null) {
			start = now.withDayOfMonth(1).with(LocalTime.MIN);
			end = now.plusMonths(1).withDayOfMonth(1).with(LocalTime.MIN).minusNanos(1);
		} else {
			start = now.withMonth(month).withDayOfMonth(1).with(LocalTime.MIN);
			end = start.plusMonths(1).withDayOfMonth(1).with(LocalTime.MIN).minusNanos(1);
		}

		return order.orderDatetime.between(start, end);
	}

}
