package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHistoryRepository
		extends JpaRepository<StockHistory, Long>, StockHistoryCustomRepository {
}
