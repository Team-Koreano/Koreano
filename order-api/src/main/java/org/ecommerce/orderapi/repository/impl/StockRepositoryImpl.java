package org.ecommerce.orderapi.repository.impl;

import static org.ecommerce.orderapi.entity.QStock.*;
import static org.ecommerce.orderapi.entity.QStockHistory.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.StockCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockRepositoryImpl implements StockCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Map<Integer, Stock> findStocksByProductIdIn(final List<Integer> productIds) {
		List<Stock> stocks = jpaQueryFactory.selectFrom(stock)
				.leftJoin(stock.stockHistories).fetchJoin()
				.where(stock.productId.in(productIds))
				.fetch();

		return stocks.stream()
				.collect(Collectors.toMap(Stock::getProductId, Function.identity()));
	}

	@Override
	public Optional<Stock> findStockByOrderItemId(Long orderItemId) {
		return Optional.ofNullable(jpaQueryFactory.select(stock)
				.from(stockHistory)
				.leftJoin(stockHistory.stock).fetchJoin()
				.leftJoin(stockHistory.orderItem).fetchJoin()
				.where(stockHistory.orderItem.id.eq(orderItemId))
				.fetchFirst());
	}
}
