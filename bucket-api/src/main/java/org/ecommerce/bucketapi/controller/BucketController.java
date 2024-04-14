package org.ecommerce.bucketapi.controller;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.service.BucketService;
import org.ecommerce.common.vo.Response;
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
@RequestMapping("api/buckets/v1")
public class BucketController {

	private final BucketService bucketService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@GetMapping
	public Response<List<BucketDto.Response>> getBuckets() {
		final List<BucketDto> bucketDto = bucketService.getAllBuckets(USER_ID);
		return new Response<>(HttpStatus.OK.value(), bucketDto.stream()
			.map(BucketDto.Response::of)
			.toList()
		);
	}

	@PostMapping
	public Response<BucketDto.Response> addBucket(
			@RequestBody @Valid final BucketDto.Request.Add addRequest
	) {
		final BucketDto bucketDto = bucketService.addBucket(USER_ID, addRequest);
		return new Response<>(HttpStatus.OK.value(), BucketDto.Response.of(bucketDto));
		return new Response<>(
				HttpStatus.OK.value(), bucketService.addBucket(USER_ID, addRequest));
	}

	@PutMapping("/{bucketId}")
	public Response<BucketDto.Response> updateBucket(
			@PathVariable("bucketId") final Long bucketId,
			@RequestBody @Valid final BucketDto.Request.Update updateRequest
	) {
		bucketService.validateBucketByUser(USER_ID, bucketId);
		return new Response<>(
				HttpStatus.OK.value(),
				bucketService.updateBucket(bucketId, updateRequest));
	}
}
