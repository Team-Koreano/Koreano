package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.dto.BucketSummary;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderItemDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.OrderStatusHistoryDto;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.entity.enumerated.OrderStatusReason;
import org.ecommerce.orderapi.repository.OrderItemRepository;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.OrderStatusHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderDomainService {

	private final OrderStatusHistoryRepository orderStatusHistoryRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderRepository orderRepository;
	// TODO user-service 검증 : user-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후
	// TODO : 회원 유효성 검사

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 회원 번호
	 * @param bucketSummary- 주문 내용
	 * @param products- 주문 내용
	 * @param stocks- 주문 내용
	 *
	 */
	public void placeOrder(
			final Order order,
			final BucketSummary bucketSummary,
			final List<Product> products,
			final List<Stock> stocks
	) {
		validateStock(stocks, bucketSummary);
		order.place(products, bucketSummary.getQuantityMap());
	}

	/**
	 * 주문 생성 전 재고를 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param stocks- 재고를 확인할 상품 번호
	 * @param bucketSummary- 회원이 주문한 상품의 수량
	 */
	@VisibleForTesting
	public void validateStock(
			final List<Stock> stocks,
			final BucketSummary bucketSummary
	) {
		final List<Integer> productIds = bucketSummary.getProductIds();
		final Map<Integer, Integer> quantities = bucketSummary.getQuantityMap();
		if (stocks.size() != productIds.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		stocks.forEach(stock -> {
			if (!stock.hasStock(quantities.get(stock.getProductId()))) {
				throw new CustomException(INSUFFICIENT_STOCK);
			}
		});
	}

	/**
	 * 주문 목록을 조회하는 메소드입니다.
	 * <p>
	 * default : 6개월 이내의 주문 내역 조회
	 * year : 해당 년도의 주문 내역 조회
	 * <p>
	 * @author ${USER}
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
		// TODO : UserId 검증
		// TODO : 상품 정보
		// User user = getUser(userId);
		Pageable pageable = PageRequest.of(pageNumber, 5);
		return orderRepository.findOrdersByUserId(userId, year, pageable).stream()
				.map(OrderMapper.INSTANCE::OrderToDto)
				.toList();
	}

	/**
	 * 주문 상세 이력을 조회하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 상세 번호
	 * @return - 주문 상세 이력 리스트
	 */
	@Transactional(readOnly = true)
	public List<OrderStatusHistoryDto> getOrderStatusHistory(final Long orderItemId) {
		return orderStatusHistoryRepository.findAllByOrderItemId(orderItemId).stream()
				.map(OrderMapper.INSTANCE::orderStatusHistoryToDto)
				.toList();
	}

	/**
	 * 주문을 취소하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 상세 번호
	 * @return - 주문 상세
	 */
	public OrderItemDto cancelOrder(final Integer userId, final Long orderItemId) {
		// final User user = getUser(userId);
		final OrderItem orderItem =
				orderItemRepository.findOrderItemById(orderItemId, userId);
		validateOrderItem(orderItem);
		orderItem.changeStatus(OrderStatus.CANCELLED, OrderStatusReason.REFUND);
		return OrderMapper.INSTANCE.orderItemToDto(orderItem);
	}

	/**
	 * 주문 상세를 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItem- 주문 상세
	 */
	@VisibleForTesting
	public void validateOrderItem(final OrderItem orderItem) {
		if (orderItem == null) {
			throw new CustomException(NOT_FOUND_ORDER_ITEM_ID);
		}

		if (!orderItem.isCancelableStatus()) {
			throw new CustomException(MUST_CLOSED_ORDER_TO_CANCEL);
		}

		if (!orderItem.isCancellableOrderDate()) {
			throw new CustomException(TOO_OLD_ORDER_TO_CANCEL);
		}
	}
}
