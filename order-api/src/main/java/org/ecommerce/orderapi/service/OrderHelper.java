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
import org.ecommerce.orderapi.entity.User;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderHelper {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final UserServiceClient userServiceClient;
	private final BucketServiceClient bucketServiceClient;
	private final ProductServiceClient productServiceClient;

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param request- 주문 요청 정보
	 * @return - 주문
	 */
	public OrderDto createOrder(
			final Integer userId,
			final OrderDto.Request.Place request
	) {
		final User user = getUser(userId);
		final List<Bucket> buckets = getBuckets(userId, request.bucketIds());
		final BucketSummary bucketSummary = BucketSummary.create(buckets);
		final List<Product> products = getProducts(bucketSummary.getProductIds());
		final Order order = Order.of(
				user.getId(),
				user.getName(),
				request.receiveName(),
				request.phoneNumber(),
				request.address1(),
				request.address2(),
				request.deliveryComment()
		);
		orderDomainService.placeOrder(order, products, bucketSummary.getQuantityMap());
		return OrderMapper.INSTANCE.OrderToDto(orderRepository.save(order));
	}

	/**
	 * 주문을 취소하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param orderId- 주문 번호
	 * @param orderItemId- 주문 항목 번호
	 * @return - 주문
	 */
	public OrderDto cancelOrder(
			final Integer userId,
			final Long orderId,
			final Long orderItemId
	) {
		final User user = getUser(userId);
		final Order order = orderRepository.findOrderByIdAndUserId(
						user.getId(), orderId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_ORDER_ID));
		orderDomainService.cancelOrder(order, orderItemId);
		return OrderMapper.INSTANCE.OrderToDto(order);
	}

	/**
	 * 주문을 승인하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderId- 주문 번호
	 * @return - 주문
	 */
	public OrderDto approveOrder(final Long orderId) {
		final Order order = orderRepository.findOrderById(orderId).orElseThrow(
				() -> new CustomException(NOT_FOUND_ORDER_ID));
		orderDomainService.approveOrder(order);
		return OrderMapper.INSTANCE.OrderToDto(order);
	}

	/**
	 * 회원 유효성 검사 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @return - 회원
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
	 * @param userId- 회원 번호
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
		final List<Product> products = productServiceClient.getProducts(productIds)
				.stream()
				.map(ProductMapper.INSTANCE::responseToEntity)
				.filter(Product::isAvailableStatus)
				.toList();
		if (products.size() != productIds.size()) {
			throw new CustomException(NOT_AVAILABLE_PRODUCT);
		}
		return products;
	}
}