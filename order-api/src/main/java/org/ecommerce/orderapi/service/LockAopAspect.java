package org.ecommerce.orderapi.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ecommerce.orderapi.aop.StockLockInterface;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

	private final LockService lockService;

	@Around("@annotation(org.ecommerce.orderapi.aop.StockLock) && args(orderDetail)")
	public Object aroundMethod(
			ProceedingJoinPoint proceedingJoinPoint,
			StockLockInterface orderDetail
	) throws Throwable {
		lockService.lock(orderDetail.getProductId());
		try {
			return proceedingJoinPoint.proceed();
		} finally {
			lockService.unLock(orderDetail.getProductId());
		}
	}
}
