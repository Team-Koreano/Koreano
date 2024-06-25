package org.ecommerce.paymentapi.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.Redis;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig{
	private final int START_REDIS_PORT = 12_345;
	private final Redis redisServer;

	public LocalRedisConfig() {
		this.redisServer = RedisServer.builder()
			.port(START_REDIS_PORT).build();
	}

	@PostConstruct
	public void startRedis() {
		try{
			this.redisServer.start();
		}catch (Exception ignore){
		}
	}

	@PreDestroy
	public void stopRedis() {
		this.redisServer.stop();
	}
}
