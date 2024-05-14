package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderItem;

public interface OrderItemCustomRepository {
	List<OrderItem> findOrderItemsByOrderId(final long orderId);

	OrderItem findOrderItemByIdAndUserId(final long orderItemId, final Integer userId);
	OrderItem findOrderItemById(final Long orderItemId);
}
