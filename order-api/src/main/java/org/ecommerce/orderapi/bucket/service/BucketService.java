package org.ecommerce.orderapi.bucket.service;


import static org.ecommerce.orderapi.bucket.exception.BucketErrorCode.*;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BucketService {

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
	@Transactional(readOnly = true)
	public List<BucketDto> getAllBuckets(final Integer userId) {

		return bucketRepository.findAllByUserId(userId)
				.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}

	/**
	 * 장바구니에 상품을 담는 메소드입니다.
	 * <p>
	 * 상품 유효성 검사 개발 예정
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param addRequest- 장바구니에 담을 상품 정보
	 * @return BucketDto- 장바구니 정보
	 */
	public BucketDto addBucket(
			final Integer userId,
			final AddBucketRequest addRequest
	) {

		return BucketMapper.INSTANCE.toDto(
				bucketRepository.save(
						Bucket.ofAdd(
								userId,
								addRequest.seller(),
								addRequest.productId(),
								addRequest.quantity()
						)
				)
		);
	}

	/**
	 * 장바구니를 수정하는 메소드입니다.
	 * <p>
	 * 1. 수정할 장바구니가 존재하는지 검증합니다.
	 * 2. 장바구니가 회원에게 유효한지 검증합니다.
	 * 3. 상품 수량을 수정합니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param bucketId- 수정할 장바구니 번호
	 * @param modifyRequest- 수량, 옵션 둥 수정 정보
	 * @return BucketDto- 수정된 장바구니 정보
	 */
	public BucketDto modifyBucket(
			final Integer userId,
			final Long bucketId,
			final ModifyBucketRequest modifyRequest
	) {
		final Bucket bucket = bucketRepository.findByIdAndUserId(bucketId, userId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_BUCKET_ID));
		bucket.modifyQuantity(modifyRequest.quantity());
		return BucketMapper.INSTANCE.toDto(bucket);
	}

	/**
	 * 장바구니에 대한 정보를 반환하는 메소드입니다.
	 * <p>
	 * 단일 혹은 여러 장바구니에 대한 정보를 반환합니다.
	 * MS간 회원의 장바구니 정보가 필요할 때 사용됩니다.
	 *
	 * 1. 회원에게 유효한 장바구니인지 검증합니다.
	 * 2. 요청한 장바구니가 누락되지 않았는지 검증합니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param bucketIds- 정보를 조회할 장바구니 번호 리스트
	 * @return BucketDto- 장바구니 정보가 담긴 리스트
	 */
	@Transactional(readOnly = true)
	public List<BucketDto> getBuckets(final Integer userId, final List<Long> bucketIds) {

		List<Bucket> buckets = bucketRepository.findAllByIdInAndUserId(bucketIds, userId);

		if (bucketIds.size() != buckets.size()) {
			throw new CustomException(NOT_FOUND_BUCKET_ID);
		}

		return buckets.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}
}
