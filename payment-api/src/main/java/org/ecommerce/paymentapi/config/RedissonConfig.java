package org.ecommerce.paymentapi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

/*
 * RedissonClient Configuration
 */
@RequiredArgsConstructor
@Configuration
public class RedissonConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedissonClient redissonClient() {
		RedissonClient redisson = null;
		Config config = new Config();
		config.useSingleServer().setAddress(redisProperties.getREDISSON_PREFIX() + redisProperties.getHost() +
			":" + redisProperties.getPort());
		redisson = Redisson.create(config);
		return redisson;
	}
}