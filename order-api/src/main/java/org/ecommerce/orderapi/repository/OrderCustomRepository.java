package org.ecommerce.orderapi.repository;

import org.ecommerce.orderapi.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
	Page<Order> findOrdersByUserId(final Integer customerId, final Integer year,
			final Pageable pageable);
}
