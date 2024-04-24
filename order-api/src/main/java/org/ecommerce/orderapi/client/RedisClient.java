package org.ecommerce.orderapi.client;

import org.ecommerce.orderapi.entity.Stock;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisClient {

	private final RedissonClient redissonClient;
	private static final String PRE_FIX = "stock:";

	public void saveStock(Integer productId, Stock stock) {
		saveStock(productId.toString(), stock);
	}

	private void saveStock(String key, Stock stock) {
		RMap<String, Integer> stockTotalMap = redissonClient.getMap(getStockTotalKey());
		RMap<String, Integer> stockInProcessingMap = redissonClient.getMap(getInProcessingStockKey());
		stockTotalMap.put(key, stock.getTotalStock());
		stockInProcessingMap.put(key, stock.getProcessingStock());
	}

	public Stock getStock(Integer productId) {
		String key = productId.toString();
		Integer totalStock = getTotalStock(key);
		Integer inProcessingStock = getInProcessingStock(key);
		return new Stock(productId, totalStock, inProcessingStock);
	}

	private Integer getTotalStock(String key) {
		RMap<String, Integer> stockTotalMap = redissonClient.getMap(getStockTotalKey());
		return stockTotalMap.get(key);
	}

	private Integer getInProcessingStock(String key) {
		RMap<String, Integer> stockTotalMap = redissonClient.getMap(getInProcessingStockKey());
		return stockTotalMap.get(key);
	}

	private String getStockTotalKey() {
		return PRE_FIX + "total";
	}

	private String getInProcessingStockKey() {
		return PRE_FIX + "inProcessing";
	}
}
