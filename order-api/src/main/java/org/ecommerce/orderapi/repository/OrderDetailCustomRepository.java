package org.ecommerce.orderapi.repository;

import java.util.List;

import org.ecommerce.orderapi.entity.OrderDetail;

public interface OrderDetailCustomRepository {
	List<OrderDetail> findOrderDetailsByOrderId(long orderId);
}
