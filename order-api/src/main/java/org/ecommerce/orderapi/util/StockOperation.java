package org.ecommerce.orderapi.util;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Stock;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;

public class StockOperation {

	private static final String STOCK_KEY = "stock:total";

	public static void setStock(
			final RTransaction transaction,
			final Stock stock
	) {
		String key = getStockKey(stock.getProductId());
		transaction.getBucket(key).set(stock.getTotal());
	}

	public static Optional<Stock> getStock(
			final RedissonClient redissonClient,
			final Integer productId
	) {
		String key = getStockKey(productId);
		Integer totalStock = (Integer)redissonClient.getBucket(key).get();
		if (totalStock == null) {
			return Optional.empty();
		} else {
			return Optional.of(new Stock(productId, totalStock));
		}
	}

	public static List<Stock> getStocks(
			final RedissonClient redissonClient,
			final List<Integer> productIds
	) {
		return productIds.stream()
				.map(productId -> {
					String key = getStockKey(productId);
					Integer totalStock = (Integer)redissonClient.getBucket(key).get();
					return new Stock(productId, totalStock);
				}).toList();
	}

	private static String getStockKey(final Integer key) {
		return STOCK_KEY + key.toString();
	}

}
