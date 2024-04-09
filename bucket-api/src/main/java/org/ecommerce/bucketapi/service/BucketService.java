package org.ecommerce.bucketapi.service;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDTO;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BucketService {

	private final BucketRepository bucketRepository;

	public List<BucketDTO.Response> getAllBuckets(final Integer userId) {
		final List<Bucket> buckets = bucketRepository.findAllByUserId(userId);
		return buckets.stream()
			.map(BucketDTO.Response::of)
			.toList();
	}
}
