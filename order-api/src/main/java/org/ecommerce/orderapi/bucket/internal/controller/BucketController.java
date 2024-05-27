package org.ecommerce.orderapi.bucket.internal.controller;

import java.util.List;

import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.dto.response.BucketResponse;
import org.ecommerce.orderapi.bucket.service.BucketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/buckets/v1")
public class BucketController {

	private final BucketService bucketService;

	// TODO : Circuit Breaker
	@GetMapping("/{userId}")
	public List<BucketResponse> getBuckets(
			@PathVariable("userId") final Integer userId,
			@RequestParam("bucketIds") final List<Long> bucketIds
	) {

		return bucketService.getBuckets(userId, bucketIds)
				.stream()
				.map(BucketMapper.INSTANCE::toResponse)
				.toList();
	}
}