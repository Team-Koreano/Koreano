package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderStatusHistory;

public interface OrderStatusHistoryCustomRepository {
	List<OrderStatusHistory> findAllByOrderDetailId(final Long orderDetailId);

}
