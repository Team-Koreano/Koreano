package org.ecommerce.orderapi.order.service;

import java.util.List;

import org.ecommerce.orderapi.order.dto.OrderItemDtoWithOrderDto;
import org.ecommerce.orderapi.order.dto.OrderItemDtoWithOrderStatusHistoryDtoList;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.repository.OrderItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemReadService {

	private final OrderItemRepository orderItemRepository;

	/**
	 * 주문 항목 상태 이력을 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 항목 번호
	 * @return - 주문 항목
	 */
	public OrderItemDtoWithOrderStatusHistoryDtoList getOrderItemStatusHistories(
			final Long orderItemId
	) {
		return OrderMapper.INSTANCE.toOrderItemStatusHistoryDto(
				orderItemRepository.findOrderItemById(orderItemId)
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
	 * @param pageable- 페이징 정보
	 * @return - 주문 항목 리스트
	 */
	public List<OrderItemDtoWithOrderDto> getOrderItems(
			final Integer sellerId,
			final Integer month,
			final Pageable pageable
	) {
		return getPageContent(
				orderItemRepository.findOrderItemsBySellerIdAndMonth(sellerId, month),
				pageable).stream()
				.map(OrderMapper.INSTANCE::toOrderItemDtoWithOrderDto)
				.toList();
	}

	/**
	 * 주문 항목 리스트 페이징 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItems- 주문 항목 리스트
	 * @param pageable- 페이징 정보
	 * @return - 주문 항목 리스트
	 */
	@VisibleForTesting
	public List<OrderItem> getPageContent(
			final List<OrderItem> orderItems,
			final Pageable pageable
	) {
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), orderItems.size());
		return orderItems.subList(start, end);
	}
}
