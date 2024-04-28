package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.dto.BucketDto.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.RedisClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.type.OrderStatus;
import org.ecommerce.orderapi.entity.type.ProductStatus;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final BucketServiceClient bucketServiceClient;
	private final OrderRepository orderRepository;
	private final RedisClient redisClient;
	private final StockService stockService;

	private final static Integer DELIVERY_FEE = 0;

	// TODO user-service 검증 : user-service 구축 이후
	// TODO bucket-service 상품 가져오기, 장바구니 검증
	// TODO product-service  재고 및 상품 검증 : product-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후

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
	public List<BucketDto> getBuckets(
			final Integer userId,
			final List<Long> bucketIds
	) {
		return bucketServiceClient.getBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::responseToDto)
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
		List<Stock> stocks = redisClient.getStocks(productIds);

		stocks.forEach(stock -> {
			if (stock.getTotal() == null || stock.getProcessingCnt() == null) {
				throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
			}
			if (stock.getAvailableStock() < quantities.get(stock.getProductId())) {
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
			if (product == null) {
				throw new CustomException(NOT_FOUND_PRODUCT_ID);
			}
			if (product.getStatus() != ProductStatus.AVAILABLE) {
				throw new CustomException(NOT_AVAILABLE_PRODUCT);
			}
		});
	}

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
	@Transactional
	public OrderDto placeOrder(
			final Integer userId,
			final OrderDto.Request.Place request
	) {
		final List<BucketDto> bucketDtos = getBuckets(userId, request.bucketIds());
		final List<Integer> productIds = toProductIds(bucketDtos);
		final Map<Integer, Integer> productIdToQuantityMap = toProductIdToQuantityMap(bucketDtos);
		final List<Product> products = redisClient.getProducts(productIds);

		validateStock(productIds, productIdToQuantityMap);
		validateProduct(products);

		Order order = Order.ofPlace(
				userId,
				request.receiveName(),
				request.phoneNumber(),
				request.address1(),
				request.address2(),
				request.deliveryComment()
		);

		List<OrderDetail> orderDetails = placeOrderDetails(
				order,
				productIdToQuantityMap,
				products
		);

		recordOrderStatusHistory(orderDetails);
		order.attachOrderDetails(orderDetails);
		orderRepository.save(order);
		stockService.increaseInProcessingStocks(orderDetails);
		return OrderMapper.INSTANCE.toDto(order);
	}

	// TODO : 배송비 우선 무료로 고정, 추후 seller에서 정책 설정

	/**
	 * 주문 상세를 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param order- 주문 정보
	 * @param productIdToQuantityMap- Map, key:productId, value:quantity
	 * @param products- 상품 정보
	 * @return List<OrderDetail>- 주문 상세 리스트
	 */
	@VisibleForTesting
	public List<OrderDetail> placeOrderDetails(
			final Order order,
			final Map<Integer, Integer> productIdToQuantityMap,
			final List<Product> products
	) {
		return products.stream()
				.map(product -> {
					Integer productId = product.getId();
					return OrderDetail.ofPlace(
							order,
							product.getId(),
							product.getPrice(),
							productIdToQuantityMap.get(productId),
							DELIVERY_FEE,
							product.getSeller()
					);
				})
				.toList();
	}

	/**
	 * 주문 상세에 대한 상태 이력을 기록하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderDetails - 주문 상세 리스트
	*/
	@VisibleForTesting
	public void recordOrderStatusHistory(final List<OrderDetail> orderDetails) {
		List<OrderStatusHistory> orderStatusHistories = orderDetails.stream()
				.map(orderDetail -> OrderStatusHistory.ofRecord(
						orderDetail,
						OrderStatus.OPEN
				))
				.toList();

		IntStream.range(0, orderDetails.size())
				.forEach(i -> orderDetails.get(i)
						.recordOrderStatusHistory(orderStatusHistories.get(i)));
	}
}
