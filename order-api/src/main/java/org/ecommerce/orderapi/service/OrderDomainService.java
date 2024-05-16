package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
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
	 * 주문을 취소하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 * @param orderItemId- 주문 항목 번호
	 */
	public void cancelOrder(final Order order, final Long orderItemId) {
		order.cancelItem(orderItemId);
	}

	/**
	 * 주문을 완료하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 * @param orderItemIds- 완료될 주문 항목 번호
	 */
	public void completeOrder(final Order order, final Set<Long> orderItemIds) {
		order.complete(orderItemIds);
	}

	/**
	 * 주문을 승인하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 */
	public void approveOrder(final Order order) {
		order.approve();
	}

	/**
	 * 재고를 검증하는 메소드입니다.
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
}
