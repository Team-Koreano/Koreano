package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderItem;

public interface OrderItemCustomRepository {
	List<OrderItem> findOrderItemsByOrderId(final long orderId);

	OrderItem findOrderItemById(final long orderItemId, final Integer userId);
}
