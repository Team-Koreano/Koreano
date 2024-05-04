package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {

	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	/**
	 * 상세 주문들의 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 */
	@StockLock
	public List<StockDto> decreaseStocks(final Long orderId) {
		return null;
		// return orderDetails.stream()
		// 		.map(orderDetail -> {
		// 			final Integer productId = orderDetail.getProductId();
		// 			final Integer quantity = orderDetail.getQuantity();
		// 			final Stock stock = stockRepository.findByProductId(productId)
		// 					.orElseThrow(
		// 							() -> new CustomException(
		// 									INSUFFICIENT_STOCK_INFORMATION));
		// 			if (!stock.hasStock(quantity)) {
		// 				throw new CustomException(INSUFFICIENT_STOCK);
		// 			}
		// 			stock.decreaseTotalStock(quantity, orderDetail.getId());
		// 			return StockMapper.INSTANCE.toStockDto(stock);
		// 		}).toList();
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
