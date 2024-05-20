package org.ecommerce.orderapi.order.repository.impl;

import static org.ecommerce.orderapi.order.entity.QOrder.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.repository.OrderCustomRepository;
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
				.leftJoin(order.orderItems).fetchJoin()
				.where(order.userId.eq(userId),
						generateDateCondition(year))
				.orderBy(order.orderDatetime.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> countQuery = jpaQueryFactory
				.select(order.count())
				.from(order)
				.where(order.userId.eq(userId),
						generateDateCondition(year));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<Order> findOrderByIdAndUserId(
			final Integer userId,
			final Long orderId
	) {
		return Optional.ofNullable(jpaQueryFactory.selectFrom(order)
				.leftJoin(order.orderItems).fetchJoin()
				.where(order.id.eq(orderId),
						userIdEq(userId))
				.fetchFirst());
	}

	@Override
	public Optional<Order> findOrderById(Long orderId) {
		return findOrderByIdAndUserId(null, orderId);
	}

	private BooleanExpression userIdEq(final Integer userId) {
		return userId == null ? null : order.userId.eq(userId);
	}

	private BooleanExpression generateDateCondition(final Integer year) {
		if (year == null) {
			return order.orderDatetime.after(LocalDateTime.now().minusMonths(6));
		}
		final LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
		final LocalDateTime endOfYear = startOfYear.plusYears(1).minusNanos(1);
		return order.orderDatetime.between(startOfYear, endOfYear);
	}

}
