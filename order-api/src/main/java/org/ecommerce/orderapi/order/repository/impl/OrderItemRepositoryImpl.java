package org.ecommerce.orderapi.order.repository.impl;

import static org.ecommerce.orderapi.order.entity.QOrder.*;
import static org.ecommerce.orderapi.order.entity.QOrderItem.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.repository.OrderItemCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderItemRepositoryImpl implements OrderItemCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public OrderItem findOrderItemById(final Long orderItemId) {
		return jpaQueryFactory.selectFrom(orderItem)
				.leftJoin(orderItem.orderStatusHistories).fetchJoin()
				.where(orderItem.id.eq(orderItemId))
				.fetchFirst();
	}

	@Override
	public List<OrderItem> findOrderItemsBySellerIdAndMonth(
			final Integer sellerId,
			final Integer month,
			final Integer pageNumber,
			final Integer pageSize
	) {
		return jpaQueryFactory
				.selectFrom(orderItem)
				.leftJoin(orderItem.order, order).fetchJoin()
				.where(orderItem.sellerId.eq(sellerId),
						generateDateCondition(month))
				.orderBy(orderItem.order.orderDatetime.desc())
				.limit(pageSize)
				.offset((long)(pageNumber - 1) * pageSize)
				.fetch();
	}

	@Override
	public Long countOrderItemsBySellerIdAndMonth(
			final Integer sellerId,
			final Integer month
	) {
		return jpaQueryFactory
				.select(orderItem.count())
				.from(orderItem)
				.leftJoin(orderItem.order, order).fetchJoin()
				.where(orderItem.sellerId.eq(sellerId),
						generateDateCondition(month))
				.fetchOne();
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
