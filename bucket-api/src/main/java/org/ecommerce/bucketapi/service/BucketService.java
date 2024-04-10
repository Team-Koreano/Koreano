package org.ecommerce.bucketapi.service;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BucketService {

	private final BucketRepository bucketRepository;

	// TODO : 회원 검증 로직 추가 (crud)
	// TODO : 상품 검증 로직 추가 (crud)
	// TODO : Rebase 이후 Exception Refactoring

	public void validateBucketByUser(final Integer userId, final Integer productId) {
		if (bucketRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new RuntimeException("이미 장바구니에 존재하는 상품입니다.");
		}
	}

	public List<BucketDto.Response> getAllBuckets(final Integer userId) {
		final List<Bucket> buckets = bucketRepository.findAllByUserId(userId);
		return buckets.stream()
			.map(BucketDto.Response::of)
			.toList();
	}

	public BucketDto.Response addBucket(final Integer userId, final BucketDto.Request.Add addRequest) {
		final Bucket newBucket = Bucket.ofAdd(
			userId,
			addRequest.seller(),
			addRequest.productId(),
			addRequest.quantity()
		);

		bucketRepository.save(newBucket);
		return BucketDto.Response.of(newBucket);
	}
}
