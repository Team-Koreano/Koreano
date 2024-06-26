package org.ecommerce.paymentapi.config;

import org.ecommerce.redis.config.RedisSingleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.Redis;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig{
	private RedisSingleInfo redisSingleInfo;
	private Redis redisServer;

	@Autowired
	public LocalRedisConfig(RedisSingleInfo redisSingleInfo) {
		this.redisSingleInfo = redisSingleInfo;
		this.redisServer = new RedisServer(redisSingleInfo.getPort());
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
