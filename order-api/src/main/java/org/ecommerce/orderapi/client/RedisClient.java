package org.ecommerce.orderapi.client;

import static org.ecommerce.orderapi.exception.StockErrorCode.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.entity.Stock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisClient {

	private final RedisTemplate<String, Object> redisTemplate;
	private static final ObjectMapper mapper = new ObjectMapper();

	public void put(Integer key, Stock stock) {
		put(key.toString(), stock);
	}

	private void put(String key, Stock stock) {
		try {
			redisTemplate.opsForValue().set(key, mapper.writeValueAsString(stock));
		} catch (JsonProcessingException e) {
			throw new CustomException(STOCK_SERIALIZATION_FAIL);
		}
	}

	public <T> T get(Integer key, Class<T> clazz) {
		return get(key.toString(), clazz);
	}

	private <T> T get(String key, Class<T> clazz) {
		String redisValue = (String)redisTemplate.opsForValue().get(key);
		if (ObjectUtils.isEmpty(redisValue)) {
			return null;
		} else {
			try {
				return mapper.readValue(redisValue, clazz);
			} catch (JsonProcessingException e) {
				throw new CustomException(STOCK_DESERIALIZATION_FAIL);
			}
		}
	}
}
