package org.ecommerce.bucketapi.external.controller;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.dto.BucketMapper;
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
@RequestMapping("/api/external/buckets/v1")
public class BucketController {

	private final BucketService bucketService;

	// todo jwt 도입 후 로직 변경
	private final static Integer USER_ID = 1;

	@GetMapping
	public Response<List<BucketDto.Response>> getBuckets() {

		return new Response<>(HttpStatus.OK.value(),
				bucketService.getAllBuckets(USER_ID)
						.stream()
						.map(BucketMapper.INSTANCE::toResponse)
						.toList()
		);
	}

	@PostMapping
	public Response<BucketDto.Response> addBucket(
			@RequestBody @Valid final BucketDto.Request.Add addRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketService.addBucket(USER_ID, addRequest)
				)
		);
	}

	@PutMapping("/{bucketId}")
	public Response<BucketDto.Response> updateBucket(
			@PathVariable("bucketId") final Long bucketId,
			@RequestBody @Valid final BucketDto.Request.Modify modifyRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				BucketMapper.INSTANCE.toResponse(
						bucketService.modifyBucket(USER_ID, bucketId, modifyRequest)
				)
		);
	}
}