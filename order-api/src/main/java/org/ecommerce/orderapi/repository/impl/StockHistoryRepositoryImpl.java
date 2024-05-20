package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QStockHistory.*;

import org.ecommerce.orderapi.entity.StockHistory;
import org.ecommerce.orderapi.repository.StockHistoryCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockHistoryRepositoryImpl implements StockHistoryCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public StockHistory findStockHistoryByOrderItemId(final Long orderItemId) {
		return jpaQueryFactory.selectFrom(stockHistory)
				.leftJoin(stockHistory.stock).fetchJoin()
				.where(stockHistory.orderItem.id.eq(orderItemId))
				.fetchOne();
	}
}
