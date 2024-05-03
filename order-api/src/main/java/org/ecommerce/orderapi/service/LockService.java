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

	public void lock() {
		RLock lock = redissonClient.getLock(TRANSACTION_LOCK);
		log.debug("Trying lock for transaction");

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

	public void unLock() {
		log.debug("Unlock for transaction");
		redissonClient.getLock(TRANSACTION_LOCK).unlock();
	}
}
