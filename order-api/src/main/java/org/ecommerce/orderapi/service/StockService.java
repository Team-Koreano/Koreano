package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.ProductStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.aop.StockLock;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.StockHistory;
import org.ecommerce.orderapi.repository.StockHistoryRepository;
import org.ecommerce.orderapi.util.ProductOperation;
import org.ecommerce.orderapi.util.StockOperation;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
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

	private final RedissonClient redissonClient;
	private final StockHistoryRepository stockHistoryRepository;

	/**
	 * 상세 주문들의 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetails- 주문 상세 리스트
	 */
	public void decreaseStocks(final List<OrderDetail> orderDetails) {
		orderDetails.forEach(orderDetail -> {
			if (!decreaseStock(orderDetail)) {
				// TODO 처리된 재고 rollback
			}
		});
	}

	/**
	 * 재고를 감소시키는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @return - 반환 값 설명 텍스트
	 */
	@VisibleForTesting
	@StockLock
	public boolean decreaseStock(final OrderDetail orderDetail) {
		final Integer productId = orderDetail.getProductId();
		final Integer quantity = orderDetail.getQuantity();
		final Stock stock = StockOperation.getStock(redissonClient, productId)
				.orElseThrow(
						() -> new CustomException(INSUFFICIENT_STOCK_INFORMATION));

		if (!stock.hasStock(quantity)) {
			throw new CustomException(INSUFFICIENT_STOCK);
		}
		stock.decreaseTotalStock(quantity);

		RTransaction transaction = redissonClient
				.createTransaction(TransactionOptions.defaults());
		try {
			StockOperation.setStock(transaction, stock);
			checkSoldOut(transaction, stock);
			saveStockHistory(orderDetail);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			log.info("Error occurred while decrease stock: {}", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 상품 매진을 확인하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param transaction- Redisson 트랜잭션
	 * @param stock- 재고 객체
	 */
	@VisibleForTesting
	public void checkSoldOut(
			final RTransaction transaction,
			final Stock stock
	) {
		if (stock.isSoldOut()) {
			Product product = ProductOperation
					.getProduct(redissonClient, stock.getProductId())
					.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT_ID));
			product.changeStatus(OUT_OF_STOCK);
			ProductOperation.setProduct(transaction, product);
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
