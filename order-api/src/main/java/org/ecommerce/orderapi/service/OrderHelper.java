package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.ProductServiceClient;
import org.ecommerce.orderapi.client.UserServiceClient;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.BucketSummary;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.ProductMapper;
import org.ecommerce.orderapi.dto.UserMapper;
import org.ecommerce.orderapi.entity.Bucket;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.User;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderHelper {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final UserServiceClient userServiceClient;
	private final BucketServiceClient bucketServiceClient;
	private final ProductServiceClient productServiceClient;
	private final StockRepository stockRepository;

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param request- 주문 요청 정보
	 * @return - 반환 값 설명 텍스트
	 */
	@Transactional
	public OrderDto createOrder(
			final Integer userId,
			final OrderDto.Request.Place request
	) {
		final User user = getUser(userId);
		final List<Bucket> buckets = getBuckets(userId, request.bucketIds());
		final BucketSummary bucketSummary = BucketSummary.create(buckets);
		final List<Product> products = getProducts(bucketSummary.getProductIds());
		final List<Stock> stocks = getStock(bucketSummary.getProductIds());
		final Order order = Order.of(
				user.getId(),
				user.getName(),
				request.receiveName(),
				request.phoneNumber(),
				request.address1(),
				request.address2(),
				request.deliveryComment()
		);
		orderDomainService.placeOrder(order, bucketSummary, products, stocks);
		return OrderMapper.INSTANCE.OrderToDto(orderRepository.save(order));
	}

	/**
	 * 회원 유효성 검사 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @return - 유저
	 */
	@VisibleForTesting
	public User getUser(final Integer userId) {
		// API (PostMan) 테스트를 위한 코드
		// return new User(1, "회원 이름");
		return UserMapper.INSTANCE.responseToUser(userServiceClient.getUser(userId));
	}

	/**
	 * 장바구니 유효성 검사 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId-    주문을 생성하는 회원 번호
	 * @param bucketIds- 장바구니 번호 리스트
	 *
	 * @return - 장바구니 리스트
	 */
	@VisibleForTesting
	public List<Bucket> getBuckets(
			final Integer userId,
			final List<Long> bucketIds
	) {
		return bucketServiceClient.getBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::responseToBucket)
				.toList();
	}

	/**
	 * 상품 유효성 검사 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productIds- 상품 번호 리스트
	 * @return - 상품 리스트
	 */
	@VisibleForTesting
	public List<Product> getProducts(
			final List<Integer> productIds
	) {
		// API (PostMan) 테스트를 위한 코드
		// return List.of(
		// 		new Product(
		// 				101,
		// 				"에디오피아 이가체프",
		// 				10000,
		// 				1,
		// 				"seller1",
		// 				AVAILABLE
		// 		),
		// 		new Product(
		// 				102,
		// 				"과테말라 안티구아",
		// 				20000,
		// 				2,
		// 				"seller2",
		// 				AVAILABLE
		// 		)
		// );
		List<Product> products = productServiceClient.getProducts(productIds)
				.stream()
				.map(ProductMapper.INSTANCE::responseToEntity)
				.filter(Product::isAvailableStatus)
				.toList();
		if (products.size() != productIds.size()) {
			throw new CustomException(NOT_AVAILABLE_PRODUCT);
		}
		return products;
	}

	/**
	 * 재고를 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productIds- 상품 번호 리스트
	 * @return - 재고 리스트
	 */
	@VisibleForTesting
	public List<Stock> getStock(final List<Integer> productIds) {
		List<Stock> stocks = stockRepository.findByProductIdIn(productIds);
		if (stocks.size() != productIds.size()) {
			throw new CustomException(INSUFFICIENT_STOCK_INFORMATION);
		}
		return stocks;
	}
}
