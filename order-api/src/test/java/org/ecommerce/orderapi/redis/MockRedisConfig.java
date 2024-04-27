package org.ecommerce.orderapi.redis;

import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class MockRedisConfig {
	@Bean
	public RedissonClient mockRedissonClient() {
		return Mockito.mock(RedissonClient.class);
	}
}
