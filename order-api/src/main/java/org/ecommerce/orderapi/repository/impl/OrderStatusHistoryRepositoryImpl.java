package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QOrderItem.*;
import static org.ecommerce.orderapi.entity.QOrderStatusHistory.*;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.ecommerce.orderapi.repository.OrderStatusHistoryCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderStatusHistoryRepositoryImpl implements
		OrderStatusHistoryCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<OrderStatusHistory> findAllByOrderItemId(Long orderItemId) {
		return jpaQueryFactory.selectFrom(orderStatusHistory)
				.where(orderItem.id.eq(orderItemId))
				.orderBy(orderStatusHistory.statusChangeDatetime.asc())
				.fetch();
	}
}
