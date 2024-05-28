package org.ecommerce.orderapi.stock.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.ecommerce.orderapi.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository
		extends JpaRepository<Stock, Integer>, StockCustomRepository {
	Optional<Stock> findByProductId(final Integer productId);

	List<Stock> findByProductIdIn(final Set<Integer> productIds);
}
