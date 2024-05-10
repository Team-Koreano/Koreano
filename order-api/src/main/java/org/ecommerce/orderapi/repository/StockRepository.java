package org.ecommerce.orderapi.repository;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository
		extends JpaRepository<Stock, Integer>, StockCustomRepository {
	Optional<Stock> findByProductId(final Integer productId);

	List<Stock> findByProductIdIn(final List<Integer> productIds);
}
