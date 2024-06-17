package org.ecommerce.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LettuceClusterConfig {
	private final RedisClusterInfo redisClusterInfo;

	public RedisConnectionFactory redisConnectionFactory() {
		RedisClusterConfiguration configuration =
			new RedisClusterConfiguration(redisClusterInfo.getNodes());
		if (redisClusterInfo.getPassword() != null) {
			configuration.setPassword(RedisPassword.of(redisClusterInfo.getPassword()));
		}
	    configuration.setMaxRedirects(redisClusterInfo.getMaxRedirects());

		return new LettuceConnectionFactory(configuration);
	}


	@Bean(name = "clusterRedisTemplate")
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}