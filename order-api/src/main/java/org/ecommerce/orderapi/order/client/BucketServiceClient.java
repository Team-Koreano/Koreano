package org.ecommerce.orderapi.order.client;

import java.util.List;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.orderapi.order.dto.response.BucketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bucket-service", url = "${bucket-service.url}", configuration = FeignConfig.class)
public interface BucketServiceClient {

	@GetMapping("/{userId}")
	List<BucketResponse> getBuckets(
			@PathVariable("userId") final Integer userId,
			@RequestParam("bucketIds") final List<Long> bucketIds
	);
}
