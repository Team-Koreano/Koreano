package org.ecommerce.paymentapi.config;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.Redis;
import redis.embedded.RedisCluster;

@Configuration
public class LocalRedisConfig{
	private final int START_REDIS_PORT = 1000;
	private final int CLUSTER_AMOUNT = 3;
	private final Redis redisServer;

	public LocalRedisConfig() {
		this.redisServer = RedisCluster.builder()
			.serverPorts(
				IntStream.range(START_REDIS_PORT, START_REDIS_PORT + CLUSTER_AMOUNT)
				.boxed()
				.collect(Collectors.toList()))
			.build();
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
