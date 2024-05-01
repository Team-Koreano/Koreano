package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.exception.StockErrorCode.*;

import java.util.concurrent.TimeUnit;

import org.ecommerce.common.error.CustomException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

	private final RedissonClient redissonClient;

	private static final String TRANSACTION_LOCK = "STOCK_LOCK_KEY:";

	public void lock(Integer productId) {
		RLock lock = redissonClient.getLock(getLockKey(productId));
		log.debug("Trying lock for productId : {}", productId);

		try {
			boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
			if (!isLock) {
				log.error("======Lock acquisition failed=====");
				throw new CustomException(STOCK_TRANSACTION_LOCK);
			}
		} catch (CustomException e) {
			throw new CustomException(e.getErrorCode());
		} catch (Exception e) {
			log.error("Redis lock failed", e);
		}
	}

	public void unLock(Integer productId) {
		log.debug("Unlock for productId : {}", productId);
		redissonClient.getLock(getLockKey(productId)).unlock();
	}

	private String getLockKey(Integer productId) {
		return TRANSACTION_LOCK + productId.toString();
	}
}
