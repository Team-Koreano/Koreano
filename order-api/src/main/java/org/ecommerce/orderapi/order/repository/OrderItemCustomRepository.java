package org.ecommerce.orderapi.order.repository;

import org.ecommerce.orderapi.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemCustomRepository {

	OrderItem findOrderItemById(final Long orderItemId);

	Page<OrderItem> findOrderItemsBySellerIdAndMonth(final Integer sellerId,
			final Integer month, final Pageable pageable);
}
