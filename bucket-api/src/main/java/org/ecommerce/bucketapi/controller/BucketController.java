package org.ecommerce.bucketapi.controller;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.service.BucketService;
import org.ecommerce.common.vo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buckets")
public class BucketController {

	private final BucketService bucketService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@GetMapping
	public Response<List<BucketDto.Response>> getBuckets() {
		final List<BucketDto.Response> bucketResponse = bucketService.getAllBuckets(USER_ID);
		return new Response<>(200, bucketResponse);
	}

	@PostMapping
	public Response<BucketDto.Response> addBucket(
		@RequestBody @Valid final BucketDto.Request.Add addRequest
	) {
		bucketService.validateBucketByUser(USER_ID, addRequest.productId());
		final BucketDto.Response bucketResponse = bucketService.addBucket(USER_ID, addRequest);
		return new Response<>(200, bucketResponse);
	}
}
