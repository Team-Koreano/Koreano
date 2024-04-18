package org.ecommerce.orderapi.service;

import java.util.List;

import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final BucketServiceClient bucketServiceClient;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;

	// TODO user-service 검증 : user-service 구축 이후
	// TODO bucket-service 상품 가져오기, 장바구니 검증
	// TODO product-service  재고 및 상품 검증 : product-service 구축 이후
	// TODO payment-service 결제 과정 : payment-service 구축 이후

	/**
	 * 주문 생성에 필요한 정보를 가져오는 메소드를 호출합니다.
	 * <p>
	 * Bucket-Service 유저정보와, 장바구니 번호들을 보내서
	 * 검증 후 주문 생성에 필요한 상품 정보를 반환 받습니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 주문을 생성하는 회원 번호
	 * @param bucketIds- 회원이 주문하는 장바구니 번호가 들어있는 리스트
	 * @return - 장바구니 정보가 들어있는 BucketDto 입니다.
	*/
	public List<BucketDto> getBuckets(
			final Integer userId,
			final List<Long> bucketIds
	) {
		return bucketServiceClient.getBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}

	public OrderDto createOrder(
			final Integer userId,
			final OrderDto.Request.Create createRequest,
			final List<BucketDto> bucketDtos
	) {
		return null;
	}
}
