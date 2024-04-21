package org.ecommerce.bucketapi.service;

import static org.ecommerce.bucketapi.exception.BucketErrorCode.*;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.dto.BucketMapper;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.ecommerce.common.error.CustomException;
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

	private void validateBucketWithUserId(final Integer userId, final Bucket bucket) {
		if (!bucket.getUserId().equals(userId)) {
			throw new CustomException(INVALID_BUCKET_WITH_USER);
		}
	}

	@Transactional(readOnly = true)
	public List<BucketDto> getAllBuckets(final Integer userId) {

		return bucketRepository.findAllByUserId(userId)
				.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}

	public BucketDto addBucket(
			final Integer userId,
			final BucketDto.Request.Add addRequest
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

	public BucketDto modifyBucket(
			final Integer userId,
			final Long bucketId,
			final BucketDto.Request.Modify modifyRequest
	) {
		final Bucket bucket = bucketRepository.findById(bucketId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_BUCKET_ID));
		validateBucketWithUserId(userId, bucket);

		bucket.modifyQuantity(modifyRequest.quantity());
		return BucketMapper.INSTANCE.toDto(bucket);
	}

	@Transactional(readOnly = true)
	public List<BucketDto> getBuckets(final Integer userId, final List<Long> bucketIds) {

		List<Bucket> buckets = bucketRepository.findAllById(bucketIds)
				.stream()
				.peek(bucket -> validateBucketWithUserId(userId, bucket))
				.toList();

		if (bucketIds.size() != buckets.size()) {
			throw new CustomException(NOT_FOUND_BUCKET_ID);
		}

		return buckets.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}
}
