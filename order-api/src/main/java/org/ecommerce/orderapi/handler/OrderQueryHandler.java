package org.ecommerce.orderapi.handler;

import java.util.List;

import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderQueryHandler {

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
	 * @return - 주문 리스트
	 */
	@Transactional(readOnly = true)
	public List<OrderDto> getOrders(
			final Integer userId,
			final Integer year,
			final Integer pageNumber
	) {
		return orderRepository.findOrdersByUserId(
						userId, year, PageRequest.of(pageNumber, 5)).stream()
				.map(OrderMapper.INSTANCE::OrderToDto)
				.toList();
	}
}
