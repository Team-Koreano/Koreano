package org.ecommerce.orderapi.client;

import java.util.List;

import org.ecommerce.orderapi.dto.BucketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bucket-service", url = "${bucket-service.url}")
public interface BucketServiceClient {

	// 장바구니에 담겨있는 상품 정보를 가져옴, 검증
	@GetMapping("/{userId}")
	List<BucketDto.Response> getBuckets(
			@PathVariable("userId") final Integer userId,
			@RequestParam("bucketIds") final List<Long> bucketIds
	);
}
