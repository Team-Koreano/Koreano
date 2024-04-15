package org.ecommerce.userapi.config;

import javax.crypto.SecretKey;

import org.ecommerce.userapi.security.JwtFilter;
import org.ecommerce.userapi.security.JwtProvider;
import org.ecommerce.userapi.security.JwtUtils;
import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

	private final RedisUtils redisUtils;
	private final SecretKey secretKey;
	private final JwtProvider jwtProvider;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtUtils jwtUtils() {
		return new JwtUtils(redisUtils, secretKey);
	}

	@Bean
	public ProviderManager providerManager() {
		return new ProviderManager(jwtProvider);
	}
	@Bean
	public JwtFilter jwtFilter(){
		return new JwtFilter(providerManager(),jwtUtils(),redisUtils);
	}
}