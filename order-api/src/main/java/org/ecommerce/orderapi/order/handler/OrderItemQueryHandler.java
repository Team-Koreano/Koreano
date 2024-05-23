package org.ecommerce.orderapi.order.handler;

import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.order.util.OrderPolicyConstants.*;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.order.dto.OrderItemDto;
import org.ecommerce.orderapi.order.dto.OrderItemStatusHistoryDto;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.repository.OrderItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemQueryHandler {

	private final OrderItemRepository orderItemRepository;

	/**
	 * 주문 항목 상태 이력을 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 항목 번호
	 * @return - 주문 항목
	 */
	public OrderItemStatusHistoryDto getOrderItemStatusHistories(final Long orderItemId) {
		return OrderMapper.INSTANCE.toOrderItemStatusHistoryDto(
				orderItemRepository.findOrderItemById(orderItemId)
						.orElseThrow(() -> new CustomException(NOT_FOUND_ORDER_ITEM_ID))
		);
	}

	/**
	 * 주문 항목 리스트를 조회하는 메소드입니다.
	 * <p>
	 * seller가 주문 항목 리스트를 조회할 때 사용합니다.
	 * month는 조회할 기간입니다. default 이번 달
	 * pageSize 10으로 고정인 상태이고 추후 동적으로 변경할 예정입니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param sellerId- 판매자 번호
	 * @param month- 조회 기간
	 * @param pageNumber- 페이지 번호
	 * @return - 주문 항목 리스트
	 */
	public List<OrderItemDto> getOrderItems(
			final Integer sellerId,
			final Integer month,
			final Integer pageNumber
	) {
		return orderItemRepository.findOrderItemsBySellerIdAndMonth(
						sellerId, month, PageRequest.of(pageNumber, ORDER_ITEM_INQUIRY_PAGE_SIZE))
				.stream()
				.map(OrderMapper.INSTANCE::toDto)
				.toList();
	}
}
