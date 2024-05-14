package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QOrderItem.*;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.repository.OrderItemCustomRepository;
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
	public List<OrderItem> findOrderItemsByOrderId(long orderId) {
		return jpaQueryFactory.selectFrom(orderItem)
				.leftJoin(orderItem.orderStatusHistories).fetchJoin()
				.where(orderItem.order.id.eq(orderId))
				.fetch();
	}

	@Override
	public OrderItem findOrderItemByIdAndUserId(
			final long orderItemId,
			final Integer userId
	) {
		return jpaQueryFactory.selectFrom(orderItem)
				.leftJoin(orderItem.orderStatusHistories).fetchJoin()
				.where(orderItem.id.eq(orderItemId),
						userIdEq(userId))
				.fetchFirst();
	}

	@Override
	public OrderItem findOrderItemById(Long orderItemId) {
		return jpaQueryFactory.selectFrom(orderItem)
				.leftJoin(orderItem.orderStatusHistories).fetchJoin()
				.where(orderItem.id.eq(orderItemId))
				.fetchFirst();
	}

	private BooleanExpression userIdEq(final Integer userId) {
		return userId == null ? null : orderItem.order.userId.eq(userId);
	}
}
