package org.ecommerce.orderapi.order.repository;

import org.ecommerce.orderapi.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository
		extends JpaRepository<Order, Long>, OrderCustomRepository {
}
