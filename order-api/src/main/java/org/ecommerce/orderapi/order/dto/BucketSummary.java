package org.ecommerce.orderapi.order.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ecommerce.orderapi.order.entity.Bucket;

import lombok.Getter;

@Getter
public class BucketSummary {
	private List<Integer> productIds;
	private Map<Integer, Integer> quantityMap;

	public static BucketSummary create(List<Bucket> buckets) {
		BucketSummary bucketSummary = new BucketSummary();
		List<Integer> productIds = new ArrayList<>();
		Map<Integer, Integer> quantityMap = new HashMap<>();

		buckets.forEach(bucketDto -> {
			productIds.add(bucketDto.getProductId());
			quantityMap.put(bucketDto.getProductId(), bucketDto.getQuantity());
		});

		bucketSummary.productIds = productIds;
		bucketSummary.quantityMap = quantityMap;
		return bucketSummary;
	}
}
