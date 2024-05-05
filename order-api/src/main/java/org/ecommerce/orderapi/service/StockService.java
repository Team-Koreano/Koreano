package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.entity.enumerated.OrderStatusReason.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
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
public class StockService {

	private final StockRepository stockRepository;
	private final OrderDetailRepository orderDetailRepository;

	/**
	 * 상세 주문들의 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 */
	@StockLock
	public List<StockDto> decreaseStocks(final Long orderId) {
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

		orderDetails.forEach(
				orderDetail -> {
					final Integer productId = orderDetail.getProductId();
					final Integer quantity = orderDetail.getQuantity();
					final Stock stock = productToToStockMap.get(productId);

					if (!stock.hasStock(quantity)) {
						saveFailedOrderDetails(orderDetails);
						throw new CustomException(INSUFFICIENT_STOCK);
					}
					stock.decreaseTotalStock(quantity, orderDetail);
					orderDetail.changeStatus(CLOSED, null);
				}
		);
		return productToToStockMap.values().stream()
				.map(StockMapper.INSTANCE::toStockDto)
				.toList();
	}

	/**
	 * 재고 차감 실패에 대한 로그를 남기는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetails- 주문상세 리스트
	 */
	@Transactional
	@VisibleForTesting
	public void saveFailedOrderDetails(List<OrderDetail> orderDetails) {
		orderDetails.forEach(
				orderDetail -> orderDetail.changeStatus(CANCELLED, OUT_OF_STOCK));
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
