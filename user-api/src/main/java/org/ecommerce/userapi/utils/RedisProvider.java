package org.ecommerce.userapi.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisProvider {

	private final RedisTemplate<String, Object> redisTemplate;

	public void setData(String key, String value, Long expiredTime, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, expiredTime, timeUnit);
	}

	public String getData(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
}
