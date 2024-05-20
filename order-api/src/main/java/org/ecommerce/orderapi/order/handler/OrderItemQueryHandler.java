package org.ecommerce.orderapi.order.handler;

import org.ecommerce.orderapi.order.dto.OrderItemStatusHistoryDto;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.repository.OrderItemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemQueryHandler {

	private final OrderItemRepository orderItemRepository;

	/**
	 * 주문 항목 상태 이력을 가져오는 메소드입니다.
	 * @author ${USER}
	 *
	 * @param orderItemId- 주문 항목 번호
	 * @return - 반환 값 설명 텍스트
	 */
	@Transactional(readOnly = true)
	public OrderItemStatusHistoryDto getOrderItemStatusHistories(final Long orderItemId) {
		return OrderMapper.INSTANCE.orderItemToOrderItemStatusHistoryDto(
				orderItemRepository.findOrderItemById(orderItemId));
	}
}
