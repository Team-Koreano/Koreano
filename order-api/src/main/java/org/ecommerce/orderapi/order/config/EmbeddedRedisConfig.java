package org.ecommerce.orderapi.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Profile("dev")
@Slf4j
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() {
		try {
			redisServer = RedisServer.builder()
					.port(redisPort)
					.setting("maxmemory 128M")
					.build();
			redisServer.start();
			log.info("start EmbeddedRedis");
		} catch (Exception e) {
			// 예외 처리
			log.info("빌드 Error를 해결하기 위한 ");
		}
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}