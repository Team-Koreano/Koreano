package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {
}
