package org.ecommerce.paymentapi.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
	private static final String REDISSON_LOCK_PREFIX = "LOCK:";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;


	@Around("@annotation(org.ecommerce.paymentapi.aop.DistributedLock)")
	public void lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key = REDISSON_LOCK_PREFIX +
			CustomSpringELParser.getDynamicValue(
				signature.getParameterNames(),
				joinPoint.getArgs(),
				distributedLock.key()
			);
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean available = rLock.tryLock(
				distributedLock.waitTime(),
				distributedLock.leaseTime(),
				distributedLock.timeUnit()
			);


			if (!available) {
				log.info("분산락 획득 실패 : {}", key);
				return;
			}
			
			log.info("분산락 시작: {}", key);
			aopForTransaction.proceed(joinPoint);  // (3)
		} catch (InterruptedException e) {
			throw new InterruptedException();
		} finally {
			try {
				log.info("분산락 종료: {}", key);
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
			}
		}
	}
}