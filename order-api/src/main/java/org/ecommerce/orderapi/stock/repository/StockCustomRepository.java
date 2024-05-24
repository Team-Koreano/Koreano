package org.ecommerce.orderapi.stock.repository;

import java.util.List;
import java.util.Map;

import org.ecommerce.orderapi.stock.entity.Stock;

public interface StockCustomRepository {
	Map<Integer, Stock> findStocksByProductIdIn(final List<Integer> productIds);

	Stock findStockByOrderItemId(final Long orderItemId);
}
