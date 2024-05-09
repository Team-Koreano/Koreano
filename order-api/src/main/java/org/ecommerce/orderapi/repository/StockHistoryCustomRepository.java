package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.StockHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHistoryCustomRepository {
	StockHistory findStockHistoryByOrderDetailId(final Long orderDetailId);
}