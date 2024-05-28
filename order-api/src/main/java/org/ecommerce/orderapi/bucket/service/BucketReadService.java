package org.ecommerce.orderapi.bucket.service;

import java.util.List;

import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BucketReadService {

	private final BucketRepository bucketRepository;

	// TODO : 회원 검증 로직 추가 (crud)
	// TODO : 상품 검증 로직 추가 (cu)

	/**
	 * 회원의 장바구니 목록을 조회하는 메소드입니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @return List<BucketDto>- 장바구니 정보가 담긴 리스트
	 */
	public List<BucketDto> getAllBuckets(final Integer userId) {

		return bucketRepository.findAllByUserId(userId)
				.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}
}
