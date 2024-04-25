package org.ecommerce.orderapi;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {

	@Value("${spring.data.redis.port}")
	private int redisPort;
	private final RedisServer redisServer;

	public TestRedisConfiguration() throws IOException {
		this.redisServer = RedisServer.builder()
				.setting("maxheap 200m")
				.port(redisPort)
				.setting("bind localhost")
				.build();
	}

	@PostConstruct
	public void postConstruct() throws IOException {
		redisServer.start();
	}

	@PreDestroy
	public void preDestroy() throws IOException {
		redisServer.stop();
	}
}
