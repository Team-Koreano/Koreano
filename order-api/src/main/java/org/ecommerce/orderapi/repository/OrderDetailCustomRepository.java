package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderDetail;

public interface OrderDetailCustomRepository {
	List<OrderDetail> findOrderDetailsByOrderId(final long orderId);

	OrderDetail findOrderDetailById(final long orderDetailId, final Integer userId);
}
