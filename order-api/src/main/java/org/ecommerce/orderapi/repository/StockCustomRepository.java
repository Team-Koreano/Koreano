package org.ecommerce.orderapi.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Stock;

public interface StockCustomRepository {
	Map<Integer, Stock> findStocksByProductIdIn(final List<Integer> productIds);

	Optional<Stock> findStockByOrderItemId(final Long orderItemId);
}
