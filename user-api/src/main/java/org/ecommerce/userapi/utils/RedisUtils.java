package org.ecommerce.userapi.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RedisUtils {

	private RedisTemplate<String, Object> redisTemplate;


	public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setData(String key, String value,Long expiredTime, TimeUnit timeUnit){
		redisTemplate.opsForValue().set(key, value, expiredTime, timeUnit);
	}
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public String getData(String key){
		return (String) redisTemplate.opsForValue().get(key);
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
	public void deleteData(String key){
		redisTemplate.delete(key);
	}
}
