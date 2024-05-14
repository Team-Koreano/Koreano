package org.ecommerce.orderapi.handler;

import org.ecommerce.orderapi.dto.OrderItemStatusHistoryDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.repository.OrderItemRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemQueryHandler {

	private final OrderItemRepository orderItemRepository;

	public OrderItemStatusHistoryDto getOrderItemStatusHistories(final Long orderItemId) {
		return OrderMapper.INSTANCE.orderItemToOrderItemStatusHistoryDto(
				orderItemRepository.findOrderItemById(orderItemId));
	}
}
