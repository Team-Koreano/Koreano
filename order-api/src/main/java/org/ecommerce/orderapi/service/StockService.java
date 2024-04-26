package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.RedisClient;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.StockHistoryRepository;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

	private final RedisClient redisClient;
	private final StockHistoryRepository stockHistoryRepository;

	public void increaseStock(final Integer productId, final Integer quantity) {
		Stock stock = redisClient.getStock(productId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT_ID));

		if (!validateStock(stock)) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		if (!validateQuantity(stock.getAvailableStock(), quantity)) {
			throw new CustomException(INSUFFICIENT_STOCK);
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
