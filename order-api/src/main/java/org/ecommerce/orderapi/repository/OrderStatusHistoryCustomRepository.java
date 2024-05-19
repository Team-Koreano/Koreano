package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderStatusHistory;

public interface OrderStatusHistoryCustomRepository {
	List<OrderStatusHistory> findAllByOrderItemId(final Long orderItemId);

}
