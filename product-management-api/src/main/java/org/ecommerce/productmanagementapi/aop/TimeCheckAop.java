package org.ecommerce.productmanagementapi.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TimeCheckAop {
	@Around("@annotation(org.ecommerce.productmanagementapi.aop.TimeCheck)")
	public Object timeCheck(final ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		String methodName = method.getName();
		TimeCheck timeCheck = method.getAnnotation(TimeCheck.class);
		if (timeCheck.timeUnit() == TimeUnit.MILLISECONDS) {
			result = timeCheckMillis(joinPoint, methodName);
		} else {
			result = timeCheckNano(joinPoint, methodName);
		}
		return result;
	}

	private Object timeCheckMillis(
		final ProceedingJoinPoint joinPoint,
		final String methodName
	) throws Throwable {
		long startTime = System.currentTimeMillis();

		Object result = joinPoint.proceed();

		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;

		log.info("{} 걸린 시간 : {}ms", methodName, elapsedTime);
		return result;
	}

	private Object timeCheckNano(
		final ProceedingJoinPoint joinPoint,
		final String methodName
	) throws Throwable {
		long startTime = System.nanoTime();

		Object result = joinPoint.proceed();

		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;

		log.info("{} 걸린 시간 : {}ns", methodName, elapsedTime);
		return result;
	}
}
