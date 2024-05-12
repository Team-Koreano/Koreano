package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
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
import org.ecommerce.orderapi.repository.StockRepository;
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

	private final StockRepository stockRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;
	private final OrderItemRepository orderItemRepository;
	// TODO user-service 검증 : user-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후
	// TODO : 회원 유효성 검사

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 * @param products- 상품 리스트
	 * @param quantityMap- 상품 수량
	 *
	 */
	public void placeOrder(
			final Order order,
			final List<Product> products,
			final Map<Integer, Integer> quantityMap
	) {
		validateStock(quantityMap);
		order.place(products, quantityMap);
	}

	/**
	 * 주문 생성 전 재고를 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param quantityMap- 상품 수량
	 */
	@VisibleForTesting
	public void validateStock(final Map<Integer, Integer> quantityMap) {
		List<Stock> stocks = stockRepository.findByProductIdIn(quantityMap.keySet());
		if (stocks.size() != quantityMap.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		stocks.forEach(stock -> {
			if (!stock.hasStock(quantityMap.get(stock.getProductId()))) {
				throw new CustomException(INSUFFICIENT_STOCK);
			}
		});
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
