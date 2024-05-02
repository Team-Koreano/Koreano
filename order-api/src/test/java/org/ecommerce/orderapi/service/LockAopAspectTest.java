package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.ProductStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
		final ArgumentCaptor<Integer> lockArgumentCaptor =
				ArgumentCaptor.forClass(Integer.class);
		final ArgumentCaptor<Integer> unLockArgumentCaptor =
				ArgumentCaptor.forClass(Integer.class);
		final Map<Integer, Integer> productIdToQuantityMap = new HashMap<>();
		productIdToQuantityMap.put(101, 1);
		final Order order = Order.ofPlace(
				1,
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				List.of(
						new Product(
								101,
								"상품 이름1",
								1000,
								"seller1",
								AVAILABLE
						)
				),
				productIdToQuantityMap
		);
		final OrderDetail orderDetail = order.getOrderDetails().get(0);

		// when
		lockAopAspect.aroundMethod(proceedingJoinPoint, orderDetail);

		// then
		verify(lockService, times(1))
				.lock(lockArgumentCaptor.capture());
		verify(lockService, times(1))
				.unLock(unLockArgumentCaptor.capture());

		assertEquals(orderDetail.getProductId(), lockArgumentCaptor.getValue());
		assertEquals(orderDetail.getProductId(), unLockArgumentCaptor.getValue());
	}

	@Test
	void 예외발생_lock_Unlock() throws Throwable {
		// given
		final ArgumentCaptor<Integer> lockArgumentCaptor =
				ArgumentCaptor.forClass(Integer.class);
		final ArgumentCaptor<Integer> unLockArgumentCaptor =
				ArgumentCaptor.forClass(Integer.class);
		final Map<Integer, Integer> productIdToQuantityMap = new HashMap<>();
		productIdToQuantityMap.put(101, 1);
		final Order order = Order.ofPlace(
				1,
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				List.of(
						new Product(
								101,
								"상품 이름1",
								1000,
								"seller1",
								AVAILABLE
						)
				),
				productIdToQuantityMap
		);
		final OrderDetail orderDetail = order.getOrderDetails().get(0);
		given(proceedingJoinPoint.proceed())
				.willThrow(new CustomException(INSUFFICIENT_STOCK));

		// when
		assertThrows(CustomException.class, () ->
				lockAopAspect.aroundMethod(proceedingJoinPoint, orderDetail));

		// then
		verify(lockService, times(1))
				.lock(lockArgumentCaptor.capture());
		verify(lockService, times(1))
				.unLock(unLockArgumentCaptor.capture());

		assertEquals(orderDetail.getProductId(), lockArgumentCaptor.getValue());
		assertEquals(orderDetail.getProductId(), unLockArgumentCaptor.getValue());
	}

}
