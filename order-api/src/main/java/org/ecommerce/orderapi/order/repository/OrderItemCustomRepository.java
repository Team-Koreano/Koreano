package org.ecommerce.orderapi.order.repository;

import java.util.Optional;

import org.ecommerce.orderapi.order.entity.OrderItem;

public interface OrderItemCustomRepository {

	OrderItem findOrderItemByIdAndUserId(final long orderItemId, final Integer userId);

	Optional<OrderItem> findOrderItemById(final Long orderItemId);
}
