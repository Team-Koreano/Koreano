package org.ecommerce.orderapi.order.repository;

import java.util.List;

import org.ecommerce.orderapi.order.entity.Order;

public interface OrderCustomRepository {
	List<Order> findOrdersByUserId(final Integer userId, final Integer year);

	Order findOrderByIdAndUserId(final Integer userId, final Long orderId);

	Order findOrderById(final Long orderId);
}
