package org.ecommerce.orderapi.stock.service;

import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ecommerce.common.error.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LockAopAspectTest {

	@InjectMocks
	LockAopAspect lockAopAspect;

	@Mock
	private LockService lockService;

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Test
	void lock_Unlock() throws Throwable {
		// given
		// when
		lockAopAspect.aroundMethod(proceedingJoinPoint);

		// then
		verify(lockService, times(1)).lock();
		verify(lockService, times(1)).unLock();
	}

	@Test
	void 예외발생_lock_Unlock() throws Throwable {
		// given
		given(proceedingJoinPoint.proceed())
				.willThrow(new CustomException(INSUFFICIENT_STOCK));

		// when
		assertThrows(CustomException.class, () ->
				lockAopAspect.aroundMethod(proceedingJoinPoint));

		// then
		verify(lockService, times(1)).lock();
		verify(lockService, times(1)).unLock();
	}

}
