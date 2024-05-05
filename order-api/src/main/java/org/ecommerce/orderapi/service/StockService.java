package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.dto.OrderDetailDto;
import org.ecommerce.orderapi.dto.OrderDetailMapper;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.enumerated.OrderStatusReason;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
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
	private final OrderDetailRepository orderDetailRepository;

	/**
	 * 상세 주문들의 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 */
	@StockLock
	public List<OrderDetailDto> decreaseStocks(final Long orderId) {
		final List<OrderDetail> orderDetails =
				orderDetailRepository.findOrderDetailsByOrderId(orderId);
		if (orderDetails == null || orderDetails.isEmpty()) {
			throw new CustomException(NOT_FOUND_ORDER_DETAIL);
		}

		final List<Integer> productIds = orderDetails.stream()
				.map(OrderDetail::getProductId)
				.toList();
		final Map<Integer, Stock> productToToStockMap =
				stockRepository.findStocksByProductIdIn(productIds);
		if (productIds.size() != productToToStockMap.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}

		boolean decreaseResult = decreaseStock(orderDetails, productToToStockMap);
		saveOrderStatus(orderDetails, decreaseResult);

		return orderDetails.stream()
				.map(OrderDetailMapper.INSTANCE::toOrderDetailDto)
				.toList();
	}

	public boolean decreaseStock(
			final List<OrderDetail> orderDetails,
			final Map<Integer, Stock> stockMap
	) {
		try {
			orderDetails.forEach(
					orderDetail -> {
						final Integer productId = orderDetail.getProductId();
						final Integer quantity = orderDetail.getQuantity();
						final Stock stock = stockMap.get(productId);

						if (!stock.hasStock(quantity)) {
							throw new CustomException(INSUFFICIENT_STOCK);
						}
						stock.decreaseTotalStock(quantity, orderDetail);
					}
			);
		} catch (CustomException e) {
			log.error("Error while decrease stock : {}", e.getErrorCode());
			return false;
		}
		return true;
	}

	/**
	 * 재고 차감 실패에 대한 로그를 남기는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetails- 주문 아이디
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@VisibleForTesting
	public void saveOrderStatus(
			final List<OrderDetail> orderDetails,
			final boolean decreaseResult
	) {
		if (decreaseResult) {
			orderDetails.forEach(
					orderDetail -> orderDetail.changeStatus(CLOSED, null));
		} else {
			orderDetails.forEach(
					orderDetail -> orderDetail.changeStatus(CANCELLED,
							OrderStatusReason.OUT_OF_STOCK));
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
