package org.ecommerce.orderapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Profile({"dev"})
@Slf4j
@Configuration
public class LocalRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        try {
            redisServer = RedisServer.builder()
                    .setting("maxheap 200m")
                    .port(redisPort)
                    .setting("bind localhost")
                    .build();
            redisServer.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}