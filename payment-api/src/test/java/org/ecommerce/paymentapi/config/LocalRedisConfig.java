package org.ecommerce.paymentapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig{

	private final RedisProperties redisProperties;

	private final RedisServer redisServer;


	@Autowired
	public LocalRedisConfig(RedisProperties properties) {
		this.redisProperties = properties;
		this.redisServer = new RedisServer(redisProperties.getPort());
	}

	@PostConstruct
	public void startRedis() {
		this.redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		this.redisServer.stop();
	}
}
