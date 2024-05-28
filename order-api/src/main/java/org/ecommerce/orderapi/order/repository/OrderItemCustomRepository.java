package org.ecommerce.orderapi.order.repository;

import java.util.List;

import org.ecommerce.orderapi.order.entity.OrderItem;

public interface OrderItemCustomRepository {

	OrderItem findOrderItemById(final Long orderItemId);

	List<OrderItem> findOrderItemsBySellerIdAndMonth(final Integer sellerId, final Integer month);
}
