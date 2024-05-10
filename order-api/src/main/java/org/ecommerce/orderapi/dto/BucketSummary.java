package org.ecommerce.orderapi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class BucketSummary {
	private List<Integer> productIds;
	private Map<Integer, Integer> productIdToQuantityMap;

	public static BucketSummary create(List<BucketDto> bucketDtos) {
		BucketSummary bucketSummary = new BucketSummary();
		List<Integer> productIds = new ArrayList<>();
		Map<Integer, Integer> productIdToQuantityMap = new HashMap<>();

		bucketDtos.forEach(bucketDto -> {
			productIds.add(bucketDto.getProductId());
			productIdToQuantityMap.put(bucketDto.getProductId(), bucketDto.getQuantity());
		});

		bucketSummary.productIds = productIds;
		bucketSummary.productIdToQuantityMap = productIdToQuantityMap;
		return bucketSummary;
	}
}
