package org.ecommerce.orderapi.order.repository;

import java.util.List;

import org.ecommerce.orderapi.order.entity.Order;

public interface OrderCustomRepository {
	List<Order> findOrdersByUserIdAndYear(final Integer userId, final Integer year, final Integer pageNumber,final Integer pageSize);

	Order findOrderByIdAndUserId(final Integer userId, final Long orderId);

	Order findOrderById(final Long orderId);

	Long countOrdersByUserIdAndYear(final Integer userId, final Integer year);
}
