package org.ecommerce.bucketapi.service;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.dto.BucketMapper;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BucketService {

	private final BucketRepository bucketRepository;

	// TODO : 회원 검증 로직 추가 (crud)
	// TODO : 상품 검증 로직 추가 (cu)

	public List<BucketDto> getAllBuckets(final Integer userId) {
		final List<Bucket> buckets = bucketRepository.findAllByUserId(userId);
		return buckets.stream()
			.map(BucketMapper.INSTANCE::toDto)
			.toList();
	}

	public BucketDto addBucket(final Integer userId, final BucketDto.Request.Add addRequest) {
		final Bucket newBucket = Bucket.ofAdd(
			userId,
			addRequest.seller(),
			addRequest.productId(),
			addRequest.quantity()
		);
		final Bucket bucket = bucketRepository.save(newBucket);
		return BucketMapper.INSTANCE.toDto(bucket);
	}
}
