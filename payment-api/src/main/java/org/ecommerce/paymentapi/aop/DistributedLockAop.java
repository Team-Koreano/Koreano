package org.ecommerce.paymentapi.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

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
	private static final String REDISSON_LOCK_PREFIX = "LOCK";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;


	@Around("@annotation(org.ecommerce.paymentapi.aop.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
		String[] keys = getKeys(joinPoint, signature, distributedLock);

		if(isSingle(keys)) {
			return singleLock(keys, joinPoint, distributedLock);
		}
		return multiLock(keys, joinPoint, distributedLock);
	}

	private static boolean isSingle(String[] keys) {
		return keys.length == 1;
	}

	private static String[] getKeys(
		ProceedingJoinPoint joinPoint,
		MethodSignature signature,
		DistributedLock distributedLock
	) {
		Set<String> keys = new HashSet<>();
		IntStream.range(0, distributedLock.uniqueKey().length).forEach(i -> {
			Arrays.stream(CustomSpringELParser.getDynamicValue(
					signature.getParameterNames(),
					joinPoint.getArgs(),
					distributedLock.uniqueKey()[i]
				)).map(value -> distributedLock.lockName().name()
					.concat(REDISSON_LOCK_PREFIX)
					.concat((String) value))
				.forEach(keys::add);
		});
		return keys.toArray(String[]::new);
	}

	private Object singleLock(
		String[] keys,
		ProceedingJoinPoint joinPoint,
		DistributedLock distributedLock
	) throws Throwable {

		String key = keys[0];
		RLock rLock = redissonClient.getLock(key);
		Object result = null;

		try {
			boolean available = rLock.tryLock(
				distributedLock.waitTime(),
				distributedLock.leaseTime(),
				distributedLock.timeUnit()
			);

			if (!available) {
				log.info("분산락 획득 실패 : {}", key);
				return result;
			}

			log.info("분산락 시작: {}", key);
			result = aopForTransaction.proceed(joinPoint);  // (3)
		} catch (InterruptedException ignored) {
		} finally {
			try {
				log.info("분산락 종료: {}", key);
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
			}
		}
		return result;
	}

	private Object multiLock(
		String[] keys,
		ProceedingJoinPoint joinPoint,
		DistributedLock distributedLock
	) throws Throwable{

		StringBuilder sb = new StringBuilder();
		String totalKey = null;
		Object result = null;

		RLock multiLock = redissonClient.getMultiLock(
			Arrays.stream(keys).map((key) -> {
					sb.append(key + ' ');
					return redissonClient.getLock(key);
				})
				.toArray(RLock[]::new));

		totalKey = sb.toString();

		try {
			boolean available = multiLock.tryLock(
				distributedLock.waitTime(),
				distributedLock.leaseTime(),
				distributedLock.timeUnit()
			);

			if (!available) {
				log.info("분산락 획득 실패 : {}", totalKey);
				return null;
			}
			log.info("분산락 시작: {}", totalKey);
			result = aopForTransaction.proceed(joinPoint);

		} catch (Throwable ignored) {
		} finally {
			try {
				log.info("분산락 종료: {}", totalKey);
				multiLock.unlock();
			} catch (IllegalMonitorStateException e) {

			}
		}
		return result;

	}
}