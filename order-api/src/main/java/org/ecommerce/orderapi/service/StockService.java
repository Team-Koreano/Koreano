package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.exception.StockErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.dto.OrderItemDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.StockHistory;
import org.ecommerce.orderapi.entity.enumerated.OrderStatusReason;
import org.ecommerce.orderapi.repository.OrderItemRepository;
import org.ecommerce.orderapi.repository.StockHistoryRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {

	private final StockRepository stockRepository;
	private final StockHistoryRepository stockHistoryRepository;
	private final OrderItemRepository orderItemRepository;

	/**
	 * 상세 주문들의 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 * @param orderId- 주문 번호
	 * @return - 주문 상세 리스트
	 *
	 */
	@StockLock
	public List<OrderItemDto> decreaseStocks(final Long orderId) {
		final List<OrderItem> orderItems =
				orderItemRepository.findOrderItemsByOrderId(orderId);
		if (orderItems == null || orderItems.isEmpty()) {
			throw new CustomException(NOT_FOUND_ORDER_ITEM);
		}

		final List<Integer> productIds = orderItems.stream()
				.map(OrderItem::getProductId)
				.toList();
		final Map<Integer, Stock> productToToStockMap =
				stockRepository.findStocksByProductIdIn(productIds);
		if (productIds.size() != productToToStockMap.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}

		boolean decreaseResult = decreaseStock(orderItems, productToToStockMap);
		saveOrderStatus(orderItems, decreaseResult);

		return orderItems.stream()
				.map(OrderMapper.INSTANCE::orderItemToDto)
				.toList();
	}

	/**
	 * 재고를 차감하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItems- 변수 설명 텍스트
	 * @param stockMap- 변수 설명 텍스트
	 * @return - 재고 차감 성공 여부
	 */
	@VisibleForTesting
	public boolean decreaseStock(
			final List<OrderItem> orderItems,
			final Map<Integer, Stock> stockMap
	) {
		try {
			orderItems.forEach(
					orderItem -> {
						final Integer productId = orderItem.getProductId();
						final Integer quantity = orderItem.getQuantity();
						final Stock stock = stockMap.get(productId);

						if (!stock.hasStock(quantity)) {
							throw new CustomException(INSUFFICIENT_STOCK);
						}
						stock.decreaseTotalStock(quantity, orderItem);
					}
			);
		} catch (CustomException e) {
			log.error("Error while decrease stock : {}", e.getErrorCode());
			return false;
		}
		return true;
	}

	/**
	 * 재고 차감에 성공 여부를 주문 상세에 저장하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItems- 주문 상세 리스트
	 * @param decreaseResult- 재고 차감 성공 여부
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@VisibleForTesting
	public void saveOrderStatus(
			final List<OrderItem> orderItems,
			final boolean decreaseResult
	) {
		if (decreaseResult) {
			orderItems.forEach(
					orderItem -> orderItem.changeStatus(CLOSED, null));
		} else {
			orderItems.forEach(
					orderItem -> orderItem.changeStatus(CANCELLED,
							OrderStatusReason.OUT_OF_STOCK));
		}
	}

	/**
	 * 재고 감소 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItemId- 주문 상세 번호
	 * @return - 재고
	 */
	@StockLock
	public StockDto increaseStock(final Long orderItemId) {
		final OrderItem orderItem = orderItemRepository
				.findOrderItemById(orderItemId, null);
		validateOrderItem(orderItem);

		final StockHistory stockHistory = stockHistoryRepository
				.findStockHistoryByOrderItemId(orderItem.getId());
		validateStockHistory(stockHistory);

		final Stock stock = stockHistory.getStock();
		if (stock == null) {
			throw new CustomException(NOT_FOUND_STOCK);
		}
		stock.increaseTotalStock(orderItem);
		return StockMapper.INSTANCE.toStockDto(stock);
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
			throw new CustomException(NOT_FOUND_ORDER_ITEM);
		}

		if (!orderItem.isRefundedOrder()) {
			throw new CustomException(MUST_CANCELLED_ORDER_TO_INCREASE_STOCK);
		}
	}

	/**
	 * 재고 이력을 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param stockHistory- 재고 이력
	 */
	@VisibleForTesting
	public void validateStockHistory(final StockHistory stockHistory) {
		if (stockHistory == null) {
			throw new CustomException(NOT_FOUND_STOCK_HISTORY);
		}

		if (!stockHistory.isOperationTypeDecrease()) {
			throw new CustomException(
					MUST_DECREASE_STOCK_OPERATION_TYPE_TO_INCREASE_STOCK);
		}
	}

	/**
	 * MockData 만드는 메소드입니다.
	 * @author ${Juwon}
	 */
	public void saveMock() {
		stockRepository.saveAll(List.of(
				Stock.of(101, 10),
				Stock.of(102, 20),
				Stock.of(103, 30)
		));
	}

	/**
	 * MockData 가져오는 메소드입니다.
	 * @author ${Juwon}
	 */
	public StockDto getMockData(Integer productId) {
		return StockMapper.INSTANCE.toStockDto(stockRepository.findByProductId(productId)
				.orElseThrow(() -> new CustomException(INSUFFICIENT_STOCK_INFORMATION)));
	}
}
