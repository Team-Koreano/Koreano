package org.ecommerce.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedissonClusterConfig {
	private final RedisClusterInfo redisClusterInfo;

	@Bean
	public RedissonClient redissonClient() {
		RedissonClient redisson = null;
		Config config = new Config();

		redisClusterInfo.getNodes().forEach(config.useClusterServers()::addNodeAddress);
		redisson = Redisson.create(config);
		return redisson;
	}
}
