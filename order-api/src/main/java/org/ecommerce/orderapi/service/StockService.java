package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.RedisClient;
import org.ecommerce.orderapi.entity.Stock;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

	private final RedisClient redisClient;

	/**
	 * 주문 생성 전 재고를 확인하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productIds- 재고를 확인할 상품 번호
	 * @param quantities- 회원이 주문한 상품의 수량
	 */
	public void checkStock(
			final List<Integer> productIds,
			final Map<Integer, Integer> quantities
	) {
		List<Stock> stocks = redisClient.getStocks(productIds);

		for (Stock stock : stocks) {
			if (!validateStock(stock)) {
				throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
			}
			if (!validateQuantity(stock.getAvailableStock(), quantities.get(stock.getProductId()))) {
				throw new CustomException(INSUFFICIENT_STOCK);
			}
		}
	}

	/**
	 * 재고를 검증하는 메소드입니다.
	 * @author ${juwon}
	 *
	 * @param stock- 재고
	 * @return - 검증 결과
	*/
	@VisibleForTesting
	public boolean validateStock(final Stock stock) {
		return stock.getTotal() != null && stock.getProcessingCnt() != null;
	}

	/**
	 * 가용한 상품 수량인지 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param availableStock- 가용한 재고
	 * @param quantity- 주문 상품 수량
	 * @return - 검증 결과
	*/
	@VisibleForTesting
	public boolean validateQuantity(final Integer availableStock, final Integer quantity) {
		return availableStock >= quantity;
	}
}
