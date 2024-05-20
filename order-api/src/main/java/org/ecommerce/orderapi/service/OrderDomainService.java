package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.PaymentServiceClient;
import org.ecommerce.orderapi.client.ProductServiceClient;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.BucketSummary;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.PaymentMapper;
import org.ecommerce.orderapi.dto.ProductMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.Payment;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.event.OrderCreatedEvent;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.StockRepository;
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

	private final BucketServiceClient bucketServiceClient;
	private final ProductServiceClient productServiceClient;
	private final PaymentServiceClient paymentServiceClient;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 주문을 생성하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 주문
	 * @param request- 상품 리스트
	 *
	 */
	public OrderDto createOrder(
			final Integer userId,
			final OrderDto.Request.Create request
	) {
		final BucketSummary bucketSummary = getBucketSummary(userId, request.bucketIds());
		final List<Product> products = getProducts(bucketSummary.getProductIds());

		validateStock(bucketSummary.getQuantityMap());
		final Order order = saveOrder(userId, request, products, bucketSummary.getQuantityMap());
		paymentOrder(order);
		applicationEventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));
		return OrderMapper.INSTANCE.OrderToDto(order);
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

	@VisibleForTesting
	public Order saveOrder(
			final Integer userId,
			final OrderDto.Request.Create request,
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
		return BucketSummary.create(
				bucketServiceClient.getBuckets(userId, bucketIds)
						.stream()
						.map(BucketMapper.INSTANCE::responseToEntity)
						.toList()
		);
	}

	@VisibleForTesting
	public void paymentOrder(final Order order) {
		Payment payment = PaymentMapper.INSTANCE.paymentResponseToEntity(
				paymentServiceClient.paymentOrder(
						OrderMapper.INSTANCE.OrderToDto(order)
				)
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
}
