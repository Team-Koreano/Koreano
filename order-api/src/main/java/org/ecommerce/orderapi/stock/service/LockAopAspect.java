package org.ecommerce.orderapi.stock.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

	private final LockService lockService;

	@Around("@annotation(org.ecommerce.orderapi.stock.aop.StockLock)")
	public Object aroundMethod(
			ProceedingJoinPoint proceedingJoinPoint
	) throws Throwable {
		lockService.lock();
		try {
			return proceedingJoinPoint.proceed();
		} finally {
			lockService.unLock();
		}
	}
}
