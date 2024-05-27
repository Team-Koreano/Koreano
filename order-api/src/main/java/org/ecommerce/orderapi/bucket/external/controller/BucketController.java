package org.ecommerce.orderapi.bucket.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.dto.response.BucketResponse;
import org.ecommerce.orderapi.bucket.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/buckets/v1")
public class BucketController {

	private final BucketService bucketService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@GetMapping
	public Response<List<BucketResponse>> getBuckets() {

		return new Response<>(HttpStatus.OK.value(),
				bucketService.getAllBuckets(USER_ID)
						.stream()
						.map(BucketMapper.INSTANCE::toResponse)
						.toList()
		);
	}

	@PostMapping
	public Response<BucketResponse> addBucket(
			@RequestBody @Valid final AddBucketRequest request
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketService.addBucket(USER_ID, request)
				)
		);
	}

	@PutMapping("/{bucketId}")
	public Response<BucketResponse> updateBucket(
			@PathVariable("bucketId") final Long bucketId,
			@RequestBody @Valid final ModifyBucketRequest request
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketService.modifyBucket(USER_ID, bucketId, request)
				)
		);
	}
}