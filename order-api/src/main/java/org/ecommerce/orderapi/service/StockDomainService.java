package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.dto.StockMapper;
import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.enumerated.StockOperationResult;
import org.ecommerce.orderapi.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockDomainService {

	private final StockRepository stockRepository;

	/**
	 * 재고를 감소 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItems- 변수 설명 텍스트
	 * @param stockMap- 변수 설명 텍스트
	 * @return - 재고 차감 성공 여부
	 */
	public Set<Long> decreaseStock(
			final List<OrderItem> orderItems,
			final Map<Integer, Stock> stockMap
	) {
		return orderItems.stream()
				.filter(orderItem -> {
					StockOperationResult result = stockMap
							.get(orderItem.getProductId())
							.decreaseTotal(orderItem);
					return result == StockOperationResult.SUCCESS;
				})
				.map(OrderItem::getId)
				.collect(Collectors.toSet());
	}

	/**
	 * 재고 증가 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderItem- 주문 항목
	 * @param stock- 재고
	 */
	public void increaseStock(final OrderItem orderItem, final Stock stock) {
		stock.increaseTotal(orderItem);
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
