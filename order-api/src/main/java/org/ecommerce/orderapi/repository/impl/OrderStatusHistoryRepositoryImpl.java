package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QOrderDetail.*;
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
	public List<OrderStatusHistory> findAllByOrderDetailId(Long orderDetailId) {
		return jpaQueryFactory.selectFrom(orderStatusHistory)
				.where(orderDetail.id.eq(orderDetailId))
				.orderBy(orderStatusHistory.statusChangeDatetime.asc())
				.fetch();
	}
}
