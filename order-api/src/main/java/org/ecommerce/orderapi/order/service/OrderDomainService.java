package org.ecommerce.orderapi.order.service;

import static org.ecommerce.orderapi.bucket.exception.BucketErrorCode.*;
import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.bucket.dto.BucketSummary;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.ecommerce.orderapi.global.client.ProductServiceClient;
import org.ecommerce.orderapi.order.client.PaymentServiceClient;
import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.dto.PaymentMapper;
import org.ecommerce.orderapi.order.dto.ProductMapper;
import org.ecommerce.orderapi.order.dto.request.CreateOrderRequest;
import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.entity.Payment;
import org.ecommerce.orderapi.order.entity.Product;
import org.ecommerce.orderapi.order.event.OrderCanceledEvent;
import org.ecommerce.orderapi.order.event.OrderCreatedEvent;
import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.ecommerce.orderapi.stock.entity.Stock;
import org.ecommerce.orderapi.stock.repository.StockRepository;
import org.springframework.context.ApplicationEventPublisher;
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

	private final ProductServiceClient productServiceClient;
	private final PaymentServiceClient paymentServiceClient;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;
	private final BucketRepository bucketRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId-  주문
	 * @param request- 주문 요청 정보
	 *
	 */
	public OrderDtoWithOrderItemDtoList createOrder(
			final Integer userId,
			final CreateOrderRequest request
	) {
		final BucketSummary bucketSummary = getBucketSummary(userId, request.bucketIds());
		final List<Product> products = getProducts(bucketSummary.getProductIds());

		validateStock(bucketSummary.getQuantityMap());
		final Order order = saveOrder(
				userId, request, products, bucketSummary.getQuantityMap());

		paymentOrder(order);
		applicationEventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));
		return OrderMapper.INSTANCE.toOrderDtoWithOrderItemDtoList(order);
	}

	/**
	 * 주문을 취소하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param orderId- 주문 번호
	 * @param orderItemId- 주문 항목 번호
	 */
	public OrderDtoWithOrderItemDtoList cancelOrder(
			final Integer userId,
			final Long orderId,
			final Long orderItemId
	) {
		final Order order = orderRepository.findOrderByIdAndUserId(userId, orderId);
		validateOrder(order);
		// TODO 결제 취소 Kafka Event 추가
		applicationEventPublisher.publishEvent(new OrderCanceledEvent(orderItemId));
		return OrderMapper.INSTANCE.toOrderDtoWithOrderItemDtoList(
				order.cancelItem(orderItemId)
		);
	}

	/**
	 * 주문을 완료하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param orderId- 주문 번호
	 * @param orderItemIds- 주문 항목 번호 Set
	 */
	public void completeOrder(final Long orderId, final Set<Long> orderItemIds) {
		Order order = orderRepository.findOrderById(orderId);
		validateOrder(order);
		order.complete(orderItemIds);
	}

	@VisibleForTesting
	public Order saveOrder(
			final Integer userId,
			final CreateOrderRequest request,
			final List<Product> products,
			final Map<Integer, Integer> quantities
	) {
		return orderRepository.save(
				Order.ofCreate(
						userId,
						// TODO : JWT 회원 이름
						request.receiveName(),
						request.receiveName(),
						request.phoneNumber(),
						request.address1(),
						request.address2(),
						request.deliveryComment(),
						products,
						quantities
				)
		);
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
	public BucketSummary getBucketSummary(
			final Integer userId,
			final List<Long> bucketIds
	) {
		List<Bucket> buckets = bucketRepository.findAllByIdInAndUserId(bucketIds, userId);
		if (bucketIds.size() != buckets.size()) {
			throw new CustomException(NOT_FOUND_BUCKET_ID);
		}
		return BucketSummary.create(buckets);
	}

	/**
	 * 주문을 결제하는 메소드입니다.
	 * <p>
	 * FeignClient로 주문과 결제를 동기적으로 처리합니다.
	 * 결제가 완료되면 결제 정보를 저장합니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param order- 주문
	 */
	@VisibleForTesting
	public void paymentOrder(final Order order) {
		Payment payment = PaymentMapper.INSTANCE.paymentResponseToEntity(
				paymentServiceClient.paymentOrder(
						OrderMapper.INSTANCE.toOrderDtoWithOrderItemDtoList(order)
				)
				// (POST MAN CODE)
				// new PaymentResponse(
				// 		1L,
				// 		50000,
				// 		LocalDateTime.now(),
				// 		List.of(
				// 				new PaymentDetailResponse(
				// 						UUID.randomUUID(),
				// 						1L,
				// 						0,
				// 						10000,
				// 						10000,
				// 						LocalDateTime.now()
				// 				),
				// 				new PaymentDetailResponse(
				// 						UUID.randomUUID(),
				// 						2L,
				// 						0,
				// 						40000,
				// 						40000,
				// 						LocalDateTime.now()
				// 				)
				// 		)
				//
				// )
		);

		order.approve(
				payment.getTotalPaymentAmount(),
				payment.getPaymentDatetime(),
				payment.getPaymentDetails()
		);
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
				}
		);
	}

	@VisibleForTesting
	public void validateOrder(final Order order) {
		if (order == null) {
			throw new CustomException(NOT_FOUND_ORDER_ID);
		}
	}
}
