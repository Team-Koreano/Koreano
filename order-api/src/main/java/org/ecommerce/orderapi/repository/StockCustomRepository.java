package org.ecommerce.orderapi.repository;

import java.util.List;
import java.util.Map;

import org.ecommerce.orderapi.entity.Stock;

public interface StockCustomRepository {
	Map<Integer, Stock> findStocksByProductIdIn(final List<Integer> productIds);
}
