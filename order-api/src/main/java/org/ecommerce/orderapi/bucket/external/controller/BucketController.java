package org.ecommerce.orderapi.bucket.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.dto.response.BucketResponse;
import org.ecommerce.orderapi.bucket.service.BucketDomainService;
import org.ecommerce.orderapi.bucket.service.BucketReadService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/buckets/v1")
public class BucketController {

	private final BucketDomainService bucketDomainService;
	private final BucketReadService bucketReadService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@GetMapping
	public Response<Page<BucketResponse>> getBuckets(
			@RequestParam(name = "pageNumber", required = false, defaultValue = "0") final Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") final Integer pageSize
	) {

		return new Response<>(HttpStatus.OK.value(),
				bucketReadService.getAllBuckets(USER_ID, pageNumber, pageSize)
						.map(BucketMapper.INSTANCE::toResponse)
		);
	}

	@PostMapping
	public Response<BucketResponse> addBucket(
			@RequestBody @Valid final AddBucketRequest request
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketDomainService.addBucket(USER_ID, request)
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
						bucketDomainService.modifyBucket(USER_ID, bucketId, request)
				)
		);
	}

	@DeleteMapping("/{bucketId}")
	public Response<BucketResponse> deleteBucket(
			@PathVariable("bucketId") final Long bucketId
	) {
		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketDomainService.deleteBucket(USER_ID, bucketId)
				)
		);
	}
}