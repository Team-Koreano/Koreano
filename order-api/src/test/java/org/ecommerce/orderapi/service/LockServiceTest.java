package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.stock.exception.StockErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.concurrent.TimeUnit;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.stock.service.LockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
public class LockServiceTest {

	@InjectMocks
	private LockService lockService;

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private RLock rLock;

	@Test
	void 락_획득() throws InterruptedException {
		// given
		given(redissonClient.getLock(anyString()))
				.willReturn(rLock);
		given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
				.willReturn(true);

		// when
		// then
		assertDoesNotThrow(() -> lockService.lock());
	}

	@Test
	void 락_획득_실패() throws InterruptedException {
		// given
		given(redissonClient.getLock(anyString()))
				.willReturn(rLock);
		given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
				.willReturn(false);

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> lockService.lock());

		// then
		assertEquals(STOCK_TRANSACTION_LOCK, exception.getErrorCode());
	}

}
