package org.ecommerce.orderapi.order.repository;

import org.ecommerce.orderapi.order.entity.OrderItem;

public interface OrderItemCustomRepository {

	OrderItem findOrderItemByIdAndUserId(final long orderItemId, final Integer userId);

	OrderItem findOrderItemById(final Long orderItemId);
}
