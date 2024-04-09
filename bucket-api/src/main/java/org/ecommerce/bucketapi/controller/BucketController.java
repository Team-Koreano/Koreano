package org.ecommerce.bucketapi.controller;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.service.BucketService;
import org.ecommerce.common.vo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buckets")
public class BucketController {

	private final BucketService bucketService;

	@GetMapping
	public Response<List<BucketDto.Response>> getBuckets() {
		final Integer userId = 1;
		final List<BucketDto.Response> bucketResponse = bucketService.getAllBuckets(userId);
		return new Response<>(200, bucketResponse);
	}
}
