package org.ecommerce.orderapi.order.repository;

import org.ecommerce.orderapi.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
	Page<Order> findOrdersByUserId(final Integer userId, final Integer year,
			final Pageable pageable);

	Order findOrderByIdAndUserId(final Integer userId, final Long orderId);

	Order findOrderById(final Long orderId);
}
