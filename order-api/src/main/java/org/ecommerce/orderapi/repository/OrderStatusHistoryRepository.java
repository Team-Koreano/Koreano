package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository
		extends JpaRepository<OrderStatusHistory, Long>,
		OrderStatusHistoryCustomRepository {
}
