package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.dto.BucketDto.*;

import java.util.List;
import java.util.Map;

import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
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
	private final OrderDetailRepository orderDetailRepository;
	private final StockService stockService;
	private final ProductService productService;

	private final static Integer DELIVERY_FEE = 0;

	// TODO user-service 검증 : user-service 구축 이후
	// TODO bucket-service 상품 가져오기, 장바구니 검증
	// TODO product-service  재고 및 상품 검증 : product-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후

	/**
	 * 장바구니 유효성검사를 하는 internal API를 호출하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId-    주문을 생성하는 회원 번호
	 * @param bucketIds- 회원이 주문하는 장바구니 번호가 들어있는 리스트
	 *
	 * @return - 장바구니 정보가 들어있는 BucketDto 입니다.
	 */
	@VisibleForTesting
	public List<BucketDto> validateBucket(
			final Integer userId,
			final List<Long> bucketIds
	) {

		return bucketServiceClient.validateBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::responseToDto)
				.toList();
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

		final List<BucketDto> bucketDtos = validateBucket(userId, request.bucketIds());

		final List<Integer> productIds = toProductIds(bucketDtos);
		final Map<Integer, Integer> productIdToQuantityMap
				= toProductIdToQuantityMap(bucketDtos);

		stockService.checkStock(productIds, productIdToQuantityMap);
		final List<Product> products = productService.getProducts(productIds);

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
		order.attachOrderDetails(orderDetails);

		orderRepository.save(order);
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
	 * @return - 반환 값 설명 텍스트
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
}
