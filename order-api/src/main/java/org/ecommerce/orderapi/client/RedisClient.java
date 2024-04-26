package org.ecommerce.orderapi.client;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisClient {

	private final RedissonClient redissonClient;
	private static final String STOCK_KEY = "stock:";
	private static final String TOTAL_KEY = "total:";
	private static final String IN_PROCESSING_KEY = "inProcessing:";
	private static final String PRODUCT_KEY = "product:";

	private void addStock(
			final Integer productId,
			final Integer totalStock,
			final Integer inProcessingStock
	) {
		String key = productId.toString();
		RMap<String, Integer> stockMap = redissonClient.getMap(STOCK_KEY);
		stockMap.put(getStockTotalKey(key), totalStock);
		stockMap.put(getInProcessingStockKey(key), inProcessingStock);
	}

	public Optional<Stock> getStock(final Integer productId) {
		String key = productId.toString();
		RMap<String, Integer> map = redissonClient.getMap(STOCK_KEY);

		Integer totalStock = map.get(getStockTotalKey(key));
		Integer inProcessingStock = map.get(getInProcessingStockKey(key));

		if (totalStock == null || inProcessingStock == null) {
			return Optional.empty();
		} else {
			return Optional.of(new Stock(productId, totalStock, inProcessingStock));
		}
	}

	public List<Stock> getStocks(final List<Integer> productIds) {
		RMap<String, Integer> stockMap = redissonClient.getMap(STOCK_KEY);
		return productIds.stream()
				.map(productId -> {
					String key = productId.toString();
					return new Stock(
							productId,
							stockMap.get(getStockTotalKey(key)),
							stockMap.get(getInProcessingStockKey(key))
					);
				})
				.toList();
	}

	public void registerProduct(final Product product, final Stock stock) {
		registerProduct(product.getId().toString(), product, stock);
	}

	private void registerProduct(
			final String key,
			final Product product,
			final Stock stock
	) {
		RMap<String, Product> productMap = redissonClient.getMap(PRODUCT_KEY);
		productMap.put(key, product);
		addStock(stock.getProductId(), stock.getTotal(), stock.getProcessingCnt());
	}

	public Optional<Product> getProduct(final Integer productId) {
		String key = productId.toString();
		RMap<String, Product> productMap = redissonClient.getMap(PRODUCT_KEY);
		return Optional.ofNullable(productMap.get(key));
	}

	public List<Product> getProducts(final List<Integer> productIds) {
		RMap<String, Product> productMap = redissonClient.getMap(PRODUCT_KEY);
		return productIds.stream()
				.map(productId -> {
					String key = productId.toString();
					return productMap.get(key);
				})
				.toList();
	}

	private String getStockTotalKey(final String key) {
		return TOTAL_KEY + key;
	}

	private String getInProcessingStockKey(final String key) {
		return IN_PROCESSING_KEY + key;
	}
}
