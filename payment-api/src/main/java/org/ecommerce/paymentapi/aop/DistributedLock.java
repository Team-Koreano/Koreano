package org.ecommerce.paymentapi.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.ecommerce.paymentapi.entity.enumerate.LockName;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {


	LockName[] lockName();

	String[] keys();

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	long waitTime() default 5L;

	long leaseTime() default 3L;
}
