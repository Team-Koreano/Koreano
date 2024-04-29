package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.type.ProductStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Objects;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.RedisClient;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.StockHistory;
import org.ecommerce.orderapi.repository.StockHistoryRepository;
import org.redisson.api.RLock;
import org.redisson.api.RTransaction;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

	private final RedisClient redisClient;
	private final StockHistoryRepository stockHistoryRepository;

	/**
	 * 처리중인 재고를 증가시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetails- 주문 상세 리스트
	*/
	public void increaseInProcessingStocks(final List<OrderDetail> orderDetails) {
		orderDetails.forEach(orderDetail -> {
			final Integer productId = orderDetail.getProductId();
			final Integer quantity = orderDetail.getQuantity();
			RLock lock = redisClient.getLock(productId);
			final Stock stock = redisClient.getStock(productId)
					.orElseThrow(
							() -> new CustomException(INSUFFICIENT_STOCK_INFORMATION));

			if (!validateQuantity(stock, quantity)) {
				throw new CustomException(INSUFFICIENT_STOCK);
			}
			RTransaction transaction = redisClient.beginTransaction();
			try {
				increaseInProcessingStock(transaction, stock, quantity);
				checkSoldOut(transaction, stock, quantity);
				saveStockHistory(orderDetail);
				transaction.commit();
				lock.unlock();
			} catch (Exception e) {
				transaction.rollback();
				lock.unlock();
				log.info("Error occurred while increasing stocks: {}", e.getMessage());
			}
		});
	}

	/**
	 * stock 객체의 ProcessingCnt를 증가시키고 트랜잭션으로 묶는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param transaction- Redisson 트랜잭션
	 * @param stock- 재고 객체
	 * @param quantity- 주문 수량
	 * @return - 반환 값 설명 텍스트
	*/
	@VisibleForTesting
	public void increaseInProcessingStock(
			final RTransaction transaction,
			final Stock stock,
			final Integer quantity
	) {
		stock.increaseProcessingCnt(quantity);
		redisClient.increaseInProcessingStock(transaction, stock);
	}

	/**
	 * 주문 수량을 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param stock- 재고 객체
	 * @param quantity- 주문 수량
	 * @return - 반환 값 설명 텍스트
	*/
	@VisibleForTesting
	public boolean validateQuantity(final Stock stock, final Integer quantity) {
		return stock.getAvailableStock() > quantity;
	}

	/**
	 * 상품 매진을 확인하고, 트랜잭션으로 묶는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param transaction- Redisson 트랜잭션
	 * @param stock- 재고 객체
	 * @param quantity- 주문 수량
	*/
	@VisibleForTesting
	public void checkSoldOut(
			final RTransaction transaction,
			final Stock stock,
			final Integer quantity
	) {
		if (Objects.equals(stock.getAvailableStock(), quantity)) {
			Product product = redisClient.getProduct(stock.getProductId()).orElseThrow(
					() -> new CustomException(NOT_FOUND_PRODUCT_ID)
			);
			product.changeStatus(OUT_OF_STOCK);
			redisClient.soldOutProduct(transaction, product);
		}
	}

	/**
	 * 재고 로그를 기록하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetail- 상세 주문 객체
	*/
	@VisibleForTesting
	public void saveStockHistory(final OrderDetail orderDetail) {
		stockHistoryRepository.save(StockHistory.ofRecord(
				orderDetail.getId(),
				orderDetail.getProductId(),
				orderDetail.getQuantity()
		));
	}

}
