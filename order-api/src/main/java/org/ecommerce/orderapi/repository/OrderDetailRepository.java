package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
