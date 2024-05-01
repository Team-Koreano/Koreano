package org.ecommerce.orderapi.client;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisClient {

	private final RedissonClient redissonClient;
	private static final String STOCK_KEY = "stock:";
	private static final String TOTAL_KEY = "total:";
	private static final String IN_PROCESSING_KEY = "inProcessing:";
	private static final String PRODUCT_KEY = "product:";
	private static final String TRANSACTION_KEY = "transaction:";

	public void putStock(final Stock stock) {
		Integer key = stock.getProductId();
		RMap<String, Integer> stockMap = redissonClient.getMap(getStockKey(key));
		stockMap.put(getTotalKey(key), stock.getTotal());
		stockMap.put(getInProcessingKey(key), stock.getProcessingCnt());
	}

	public Optional<Stock> getStock(final Integer productId) {
		RMap<String, Integer> map = redissonClient.getMap(getStockKey(productId));

		Integer totalStock = map.get(getTotalKey(productId));
		Integer inProcessingStock = map.get(getInProcessingKey(productId));

		if (totalStock == null || inProcessingStock == null) {
			return Optional.empty();
		} else {
			return Optional.of(new Stock(productId, totalStock, inProcessingStock));
		}
	}

	public List<Stock> getStocks(final List<Integer> productIds) {
		return productIds.stream()
				.map(productId -> {
					RMap<String, Integer> map = redissonClient.getMap(
							getStockKey(productId));
					return new Stock(
							productId,
							map.get(getTotalKey(productId)),
							map.get(getInProcessingKey(productId))
					);
				}).toList();
	}

	public Optional<Product> getProduct(final Integer productId) {
		return Optional.ofNullable(
				(Product)redissonClient.getBucket(getProductKey(productId)).get());
	}

	public List<Product> getProducts(final List<Integer> productIds) {
		return productIds.stream()
				.map(productId -> (Product)redissonClient
						.getBucket(getProductKey(productId)).get())
				.toList();
	}

	public void setProduct(final Product product) {
		redissonClient.getBucket(getProductKey(product.getId())).set(product);
	}

	public RTransaction beginTransaction() {
		return redissonClient.createTransaction(TransactionOptions.defaults());
	}

	public void increaseInProcessingStock(final RTransaction transaction, final Stock stock) {
		Integer key = stock.getProductId();
		RMap<String, Integer> map = transaction.getMap(getStockKey(key));
		map.put(getTotalKey(key), stock.getTotal());
		map.put(getInProcessingKey(key), stock.getProcessingCnt());
	}

	public void soldOutProduct(final RTransaction transaction, final Product product) {
		Integer key = product.getId();
		transaction.getBucket(getProductKey(key)).set(product);
	}

	public RLock getLock(final Integer productId) {
		RLock lock = redissonClient.getLock(getTransactionLock(productId));
		lock.lock();
		return lock;
	}

	private String getStockKey(final Integer key) {
		return STOCK_KEY + key.toString();
	}

	private String getTotalKey(final Integer key) {
		return TOTAL_KEY + key.toString();
	}

	private String getInProcessingKey(final Integer key) {
		return IN_PROCESSING_KEY + key.toString();
	}

	private String getProductKey(final Integer key) {
		return PRODUCT_KEY + key.toString();
	}

	private String getTransactionLock(final Integer key) {
		return TRANSACTION_KEY + key.toString();
	}
}
