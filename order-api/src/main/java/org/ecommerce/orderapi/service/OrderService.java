package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.ProductServiceClient;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.BucketSummary;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.ProductMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.enumerated.ProductStatus;
import org.ecommerce.orderapi.repository.OrderRepository;
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
public class OrderService {

	private final BucketServiceClient bucketServiceClient;
	private final ProductServiceClient productServiceClient;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	// TODO user-service 검증 : user-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후
	// TODO : 회원 유효성 검사

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param request- 주문 내용
	 *
	 * @return - 생성된 주문을 반환합니다.
	 */
	public OrderDto placeOrder(
			final Integer userId,
			final OrderDto.Request.Place request
	) {
		final BucketSummary bucketSummary = getBuckets(userId, request.bucketIds());
		final List<Integer> productIds = bucketSummary.getProductIds();
		final Map<Integer, Integer> productIdToQuantityMap = bucketSummary.getProductIdToQuantityMap();

		final List<Product> products = getProducts(productIds);

		validateStock(productIds, productIdToQuantityMap);
		validateProduct(products);

		return OrderMapper.INSTANCE.toDto(
				orderRepository.save(
						Order.ofPlace(
								userId,
								request.receiveName(),
								request.phoneNumber(),
								request.address1(),
								request.address2(),
								request.deliveryComment(),
								products,
								productIdToQuantityMap
						)
				)
		);
	}

	/**
	 * 장바구니 유효성검사 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId-    주문을 생성하는 회원 번호
	 * @param bucketIds- 회원이 주문하는 장바구니 번호가 들어있는 리스트
	 *
	 * @return - 장바구니 정보가 들어있는 BucketDto 입니다.
	 */
	@VisibleForTesting
	public BucketSummary getBuckets(
			final Integer userId,
			final List<Long> bucketIds
	) {
		return BucketSummary.of(
				bucketServiceClient.getBuckets(userId, bucketIds)
						.stream()
						.map(BucketMapper.INSTANCE::responseToDto).toList());
	}

	@VisibleForTesting
	public List<Product> getProducts(
			final List<Integer> productIds
	) {
		return productServiceClient.getProducts(productIds)
				.stream()
				.map(ProductMapper.INSTANCE::responseToEntity)
				.toList();
	}

	/**
	 * 주문 생성 전 재고를 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productIds- 재고를 확인할 상품 번호
	 * @param quantities- 회원이 주문한 상품의 수량
	 */
	@VisibleForTesting
	public void validateStock(
			final List<Integer> productIds,
			final Map<Integer, Integer> quantities
	) {
		List<Stock> stocks = stockRepository.findByProductIdIn(productIds);
		if (stocks.size() != productIds.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		stocks.forEach(stock -> {
			if (!stock.hasStock(quantities.get(stock.getProductId()))) {
				throw new CustomException(INSUFFICIENT_STOCK);
			}
		});
	}

	/**
	 * 주문 생성 전 상품을 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param products - 상품 리스트
	 */
	@VisibleForTesting
	public void validateProduct(final List<Product> products) {
		products.forEach(product -> {
			if (product.getStatus() != ProductStatus.AVAILABLE) {
				throw new CustomException(NOT_AVAILABLE_PRODUCT);
			}
		});
	}
}
