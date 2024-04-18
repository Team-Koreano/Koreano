package org.ecommerce.orderapi.service;

import java.util.List;

import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketMapper;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.OrderMapper;
import org.ecommerce.orderapi.entity.Order;
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

	// TODO : 재고 확인 과정에서 상품 정보를 받아와 총 계산하여 Beanpay 값을 넣어줘야 함
	/**
	 * 주문 생성 메소드 입니다.
	 * <p>
	 * 해당 메소드가 실행되기 전 아래의 과정을 거쳐야 합니다.
	 * 1. 회원 유효성 검사
	 * 2. 장바구니 유효성 검사
	 * 3. 상품 재고 확인 및 유효성 검사
	 *
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param createRequest- Client가 입력한 주문서 내용
	 * @return - 생성된 주문 Data가 들어간 OrderDto를 반환합니다.
	*/
	public OrderDto createOrder(
			final Integer userId,
			final OrderDto.Request.Create createRequest
	) {

		return OrderMapper.INSTANCE.toDto(
				orderRepository.save(
						Order.ofCreate(
								userId,
								createRequest.receiveName(),
								createRequest.phoneNumber(),
								createRequest.address1(),
								createRequest.address2(),
								createRequest.deliveryComment(),
								null
						)
				)
		);
	}
}
