package org.ecommerce.orderapi.order.repository;

import org.ecommerce.orderapi.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository
		extends JpaRepository<OrderItem, Long>, OrderItemCustomRepository {
}
