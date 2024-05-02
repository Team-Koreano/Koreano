package org.ecommerce.orderapi.util;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Product;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;

public class ProductOperation {

	private static final String PRODUCT_KEY = "product:";

	public static void setProduct(final RTransaction transaction, final Product product) {
		String key = getProductKey(product.getId());
		transaction.getBucket(key).set(product);
	}

	public static Optional<Product> getProduct(
			final RedissonClient redissonClient,
			final Integer productId
	) {
		String key = getProductKey(productId);
		return Optional.ofNullable((Product) redissonClient.getBucket(key).get());
	}

	public static List<Product> getProducts(
			final RedissonClient redissonClient,
			final List<Integer> productIds
	) {
		return productIds.stream()
				.map(productId -> {
					String key = getProductKey(productId);
					return (Product)redissonClient.getBucket(key).get();
				}).toList();
	}

	private static String getProductKey(final Integer key) {
		return PRODUCT_KEY + key.toString();
	}

}
