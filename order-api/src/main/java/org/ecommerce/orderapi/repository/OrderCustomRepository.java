package org.ecommerce.orderapi.repository;

import java.util.Optional;

import org.ecommerce.orderapi.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
	Page<Order> findOrdersByUserId(final Integer userId, final Integer year,
			final Pageable pageable);

	Optional<Order> findOrderByIdAndUserId(final Integer userId, final Long orderId);

	Optional<Order> findOrderById(final Long orderId);
}
