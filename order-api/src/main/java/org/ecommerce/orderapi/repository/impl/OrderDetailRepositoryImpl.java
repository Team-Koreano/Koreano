package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QOrderDetail.*;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.repository.OrderDetailCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderDetailRepositoryImpl implements OrderDetailCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<OrderDetail> findOrderDetailsByOrderId(long orderId) {
		return jpaQueryFactory.selectFrom(orderDetail)
				.leftJoin(orderDetail.order).fetchJoin()
				.leftJoin(orderDetail.orderStatusHistories).fetchJoin()
				.where(orderDetail.order.id.eq(orderId))
				.fetch();
	}

	@Override
	public OrderDetail findOrderDetailById(long orderDetailId) {
		return jpaQueryFactory.selectFrom(orderDetail)
				.leftJoin(orderDetail.orderStatusHistories).fetchJoin()
				.where(orderDetail.id.eq(orderDetailId))
				.fetchOne();
	}
}
