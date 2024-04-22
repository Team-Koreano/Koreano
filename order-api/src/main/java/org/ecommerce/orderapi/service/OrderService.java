package org.ecommerce.orderapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.ProductSearchServiceClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.dto.ProductDto;
import org.ecommerce.orderapi.dto.ProductMapper;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final BucketServiceClient bucketServiceClient;
	private final ProductSearchServiceClient productManagementServiceClient;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;

	private final static Integer DELIVERY_FEE = 0;

	// TODO user-service 검증 : user-service 구축 이후
	// TODO bucket-service 상품 가져오기, 장바구니 검증
	// TODO product-service  재고 및 상품 검증 : product-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후

	/**
	 * 장바구니 유효성검사를 하는 internal API를 호출하는 메소드 입니다.
	 * <p>
	 * Bucket-Service 유저정보와, 장바구니 번호들을 보내서
	 * 검증 후 주문 생성에 필요한 상품 정보를 반환 받습니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId-    주문을 생성하는 회원 번호
	 * @param bucketIds- 회원이 주문하는 장바구니 번호가 들어있는 리스트
	 *
	 * @return - 장바구니 정보가 들어있는 BucketDto 입니다.
	 */
	private List<BucketDto> validateBucket(
			final Integer userId,
			final List<Long> bucketIds
	) {

		return bucketServiceClient.validateBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::responseToDto)
				.toList();
	}

	/**
	 * 주문하는 상품의 재고를 확인하는 internal API를 호출하는 메소드 입니다.
	 * <p>
	 * product-management-service에게 상품 번호 리스트를 보내서 해당 상품의
	 * 유효성 및 주문 수량보다 많은지 비교하고 상품 정보를 반환 받습니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param productIds- 재고를 확인할 상품 번호 리스트
	 * @param quantities- 회원이 주문한 상품의 수량
	 * @return - 주문한 상품의 정보
	 */
	private List<ProductDto> checkStock(
			final List<Integer> productIds,
			final List<Integer> quantities
	) {

		return productManagementServiceClient.checkStocks(productIds, quantities)
				.stream()
				.map(ProductMapper.INSTANCE::responseToDto)
				.toList();
	}

	// TODO : 회원 유효성 검사
	/**
	 * 주문을 생성하는 메소드 입니다.
	 * <p>
	 * 해당 메소드는 아래의 과정을 따릅니다.
	 * 1. 장바구니 유효성 검사
	 * 2. 상품 재고 확인 및 유효성 검사
	 * 3. 주문 생성
	 * 4. 주문 상세 생성
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param placeRequest- 주문 내용
	 *
	 * @return - 생성된 주문을 반환합니다.
	 */
	@Transactional
	public OrderDto placeOrder(
			final Integer userId,
			final OrderDto.Request.Place placeRequest
	) {

		final List<BucketDto> bucketDtos = validateBucket(
				userId,
				placeRequest.bucketIds()
		);
		final List<ProductDto> productDtos = checkStock(
				bucketDtos.stream()
						.map(BucketDto::getProductId)
						.toList(),
				bucketDtos.stream()
						.map(BucketDto::getQuantity)
						.toList()
		);

		Order order = orderRepository.save(
				Order.ofPlace(
						userId,
						placeRequest.receiveName(),
						placeRequest.phoneNumber(),
						placeRequest.address1(),
						placeRequest.address2(),
						placeRequest.deliveryComment()
				)
		);

		placeOrderDetails(order, bucketDtos, productDtos);

		return OrderMapper.INSTANCE.toDto(order);
	}

	// TODO : 배송비 우선 무료로 고정, 추후 seller에서 정책 설정
	/**
	 * 주문 상세를 생성하는 메소드 입니다.
	 * <p>
	 * 1. 상품 정보 매핑
	 * productDtos 리스트를 사용하여 상품의 ID와 가격을 매핑하는
	 * productIdToPriceMap을 생성합니다. 이 맵은 상품의 ID를 키로,
	 * 상품의 가격을 값으로 가집니다.
	 *
	 * 2. 주문 상세 생성
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param order- 주문 정보
	 * @param bucketDtos- 검증된 장바구니 정보
	 * @param productDtos- 상품 정보
	 * @return - 반환 값 설명 텍스트
	 */
	private void placeOrderDetails(
			final Order order,
			final List<BucketDto> bucketDtos,
			final List<ProductDto> productDtos
	) {

		final Map<Integer, Integer> productIdToPriceMap = productDtos.stream()
				.collect(Collectors.toMap(
						ProductDto::getId,
						ProductDto::getPrice
				));

		orderDetailRepository.saveAll(
				bucketDtos.stream()
						.map(bucketDto -> OrderDetail.ofPlace(
								order,
								bucketDto.getProductId(),
								productIdToPriceMap.get(bucketDto.getProductId()),
								bucketDto.getQuantity(),
								DELIVERY_FEE,
								bucketDto.getSeller()
						))
						.toList()
		);
	}
}
