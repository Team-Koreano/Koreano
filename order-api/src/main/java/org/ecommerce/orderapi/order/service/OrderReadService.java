package org.ecommerce.orderapi.order.service;

import java.util.List;

import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderReadService {

	private final OrderRepository orderRepository;

	/**
	 * 주문 목록을 조회하는 메소드입니다.
	 * <p>
	 * default : 6개월 이내의 주문 내역 조회
	 * year : 해당 년도의 주문 내역 조회
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 유저 번호
	 * @param year- 조회 연도
	 * @param pageable- 페이징 정보
	 * @return - 주문 리스트
	 */
	public List<OrderDtoWithOrderItemDtoList> getOrders(
			final Integer userId,
			final Integer year,
			final Pageable pageable
	) {
		return getPageContent(orderRepository.findOrdersByUserId(userId, year), pageable)
				.stream()
				.map(OrderMapper.INSTANCE::toOrderDtoWithOrderItemDtoList)
				.toList();
	}

	/**
	 * 주문 리스트 페이징 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orders- 주문 리스트
	 * @param pageable- 페이징 정보
	 * @return - 주문 리스트
	 */
	@VisibleForTesting
	public List<Order> getPageContent(final List<Order> orders, final Pageable pageable) {
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), orders.size());
		return orders.subList(start, end);
	}
}
