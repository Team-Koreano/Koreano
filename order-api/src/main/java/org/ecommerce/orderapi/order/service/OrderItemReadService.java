package org.ecommerce.orderapi.order.service;

import org.ecommerce.orderapi.order.dto.OrderItemDtoWithOrderDto;
import org.ecommerce.orderapi.order.dto.OrderItemDtoWithOrderStatusHistoryDtoList;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.repository.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param sellerId- 판매자 번호
	 * @param month- 조회 기간
	 * @param pageNumber- 페이지 번호
	 * @param pageSize- 페이지 크기
	 * @return - 주문 항목 리스트
	 */
	public Page<OrderItemDtoWithOrderDto> getOrderItems(
			final Integer sellerId,
			final Integer month,
			final Integer pageNumber,
			final Integer pageSize
	) {
		return new PageImpl<>(
				orderItemRepository.findOrderItemsBySellerIdAndMonth(
								sellerId, month, pageNumber, pageSize)
						.stream()
						.map(OrderMapper.INSTANCE::toOrderItemDtoWithOrderDto)
						.toList(),
				PageRequest.of(pageNumber, pageSize),
				orderItemRepository.countOrderItemsBySellerIdAndMonth(sellerId, month)
		);
	}
}
