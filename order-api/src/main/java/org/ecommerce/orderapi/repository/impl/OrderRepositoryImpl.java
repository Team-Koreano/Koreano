package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QOrder.*;
import static org.ecommerce.orderapi.entity.QOrderDetail.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.repository.OrderCustomRepository;
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
public class OrderRepositoryImpl implements OrderCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<Order> findOrdersByUserId(
			final Integer userId,
			final Integer year,
			final Pageable pageable
	) {
		List<Order> content = jpaQueryFactory
				.selectFrom(order)
				.leftJoin(order.orderDetails).fetchJoin()
				.leftJoin(orderDetail.orderStatusHistories).fetchJoin()
				.where(userIdEq(userId),
						generateDateCondition(year))
				.orderBy(order.orderDatetime.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> countQuery = jpaQueryFactory
				.select(order.count())
				.from(order)
				.where(userIdEq(userId),
						generateDateCondition(year));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression userIdEq(final Integer userId) {
		return userId == null ? null : order.userId.eq(userId);
	}

	private BooleanExpression generateDateCondition(final Integer year) {
		if (year == null) {
			return order.orderDatetime.after(LocalDateTime.now().minusMonths(6));
		} else {
			final LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
			final LocalDateTime endOfYear = startOfYear.plusYears(1).minusNanos(1);
			return order.orderDatetime.between(startOfYear, endOfYear);
		}
	}

}
