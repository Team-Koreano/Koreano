package org.ecommerce.bucketapi.controller;

import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDTO;
import org.ecommerce.bucketapi.service.BucketService;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<BucketDTO.Response>> getBuckets() {
		final Integer userId = 1;
		final List<BucketDTO.Response> bucketResponse = bucketService.getAllBuckets(userId);
		return ResponseEntity.ok(bucketResponse);
	}
}
