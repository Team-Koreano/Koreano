package org.ecommerce.bucketapi.service;

import static org.ecommerce.bucketapi.exception.BucketErrorCode.*;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
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

	@Transactional(readOnly = true)
	public void validateBucketByUser(final Integer userId, final Long bucketId) {
		if (!bucketRepository.existsByUserIdAndId(userId, bucketId)) {
			throw new CustomException(INVALID_BUCKET_WITH_USER);
		}
	}

	@Transactional(readOnly = true)
	public List<BucketDto.Response> getAllBuckets(final Integer userId) {
		return bucketRepository.findAllByUserId(userId)
				.stream()
				.map(BucketDto.Response::of)
				.toList();
	}

	public BucketDto.Response addBucket(
			final Integer userId,
			final BucketDto.Request.Add addRequest
	) {
		final Bucket newBucket = Bucket.ofAdd(
				userId,
				addRequest.seller(),
				addRequest.productId(),
				addRequest.quantity()
		);

		final Bucket bucket = bucketRepository.save(newBucket);
		return BucketDto.Response.of(bucket);
	}

	public BucketDto.Response updateBucket(
			final Long bucketId,
			final BucketDto.Request.Update updateRequest
	) {
		final Bucket bucket = bucketRepository.findById(bucketId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_BUCKET_ID));

		bucket.update(updateRequest.quantity());
		return BucketDto.Response.of(bucket);
	}
}
