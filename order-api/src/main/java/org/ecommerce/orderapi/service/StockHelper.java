package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.exception.StockErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class StockHelper {

	private final StockDomainService stockDomainService;
	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	/**
	 * 재고 증가 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderId- 주문 번호
	 * @return - 재고
	 */
	@StockLock
	public List<StockDto> decreaseStocks(final Long orderId) {
		final Order order = getOrder(orderId);
		final List<OrderItem> orderItems = getOrderItems(order);
		final Map<Integer, Stock> stockMap = getStockMap(orderItems.stream()
				.map(OrderItem::getProductId)
				.toList());

		final Set<Long> successfulOrderItemIds =
				stockDomainService.decreaseStock(orderItems, stockMap);
		orderDomainService.completeOrder(order, successfulOrderItemIds);
		return stockMap.values().stream()
				.map(StockMapper.INSTANCE::toStockDto)
				.toList();
	}

	/**
	 * 재고 감소 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 상세 번호
	 * @return - 재고
	 */
	@StockLock
	public StockDto increaseStock(final Long orderId, final Long orderItemId) {
		final Order order = getOrder(orderId);
		final OrderItem orderItem = order.getOrderItemByOrderItemId(orderItemId);
		final Stock stock = getStock(orderItem.getId());
		stockDomainService.increaseStock(orderItem, stock);
		return StockMapper.INSTANCE.toStockDto(stock);
	}

	/**
	 * 주문을 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderId- 주문 번호
	 * @return - 주문
	 */
	@VisibleForTesting
	public Order getOrder(final Long orderId) {
		final Order order = orderRepository.findOrderById(orderId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_ORDER_ID));
		if (!order.isStockOperationProcessableOrder()) {
			throw new CustomException(MUST_ORDER_ITEM_STATUS_ACCEPT_TO_DECREASE_STOCK);
		}
		return order;
	}

	/**
	 * 주문 항목 리스트를 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 * @return - 주문 항목 리스트
	 */
	@VisibleForTesting
	public List<OrderItem> getOrderItems(final Order order) {
		final List<OrderItem> orderItems = order.getOrderItems();
		if (orderItems == null || orderItems.isEmpty()) {
			throw new CustomException(NOT_FOUND_ORDER_ITEM);
		}
		return orderItems;
	}

	/**
	 * 재고 맵을 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productIds- 상품 번호 리스트
	 * @return - 재고 맵
	 */
	@VisibleForTesting
	public Map<Integer, Stock> getStockMap(final List<Integer> productIds) {
		final Map<Integer, Stock> stockMap = stockRepository.findStocksByProductIdIn(
				productIds);
		if (productIds.size() != stockMap.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		return stockMap;
	}

	/**
	 * 재고를 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 항목 번호
	 * @return - 재고
	 */
	@VisibleForTesting
	public Stock getStock(final Long orderItemId) {
		return stockRepository.findStockByOrderItemId(orderItemId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_STOCK));
	}
}
