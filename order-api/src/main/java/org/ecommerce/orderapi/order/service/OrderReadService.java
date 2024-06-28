package org.ecommerce.orderapi.order.service;

import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	 * @param pageNumber- 페이지 번호
	 * @param pageSize- 페이지 사이즈
	 * @return - 주문 리스트
	 */
	public Page<OrderDtoWithOrderItemDtoList> getOrders(
			final Integer userId,
			final Integer year,
			final Integer pageNumber,
			final Integer pageSize
	) {
		return new PageImpl<>(
				orderRepository.findOrdersByUserIdAndYear(
								userId, year, pageNumber, pageSize)
						.stream()
						.map(OrderMapper.INSTANCE::toOrderDtoWithOrderItemDtoList)
						.toList(),
				PageRequest.of(pageNumber, pageSize),
				orderRepository.countOrdersByUserIdAndYear(userId, year)
		);
	}

}
