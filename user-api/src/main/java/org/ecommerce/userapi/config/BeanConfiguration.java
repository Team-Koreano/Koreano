package org.ecommerce.userapi.config;

import java.util.Collections;

import javax.crypto.SecretKey;

import org.ecommerce.userapi.security.AuthDetailsService;
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

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtUtils jwtUtils() {
			return new JwtUtils(redisUtils, secretKey);
	}

	@Bean
	public JwtProvider jwtProvider(AuthDetailsService authDetailsService, JwtUtils jwtUtils) {
		return new JwtProvider(authDetailsService, jwtUtils);
	}

	@Bean
	public ProviderManager providerManager(JwtProvider jwtProvider) {
		return new ProviderManager(Collections.singletonList(jwtProvider));
	}

	@Bean
	public JwtFilter jwtFilter(ProviderManager providerManager, JwtUtils jwtUtils, RedisUtils redisUtils) {
		return new JwtFilter(providerManager, jwtUtils, redisUtils);
	}
}