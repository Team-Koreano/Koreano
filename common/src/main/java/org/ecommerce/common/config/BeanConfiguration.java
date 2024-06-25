package org.ecommerce.common.config;

import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.common.security.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;

@Configuration
public class BeanConfiguration {

	@Bean
	public CustomAuthProvider customAuthProvider(JwtProvider jwtProvider) {
		return new CustomAuthProvider(jwtProvider);
	}

	@Bean
	public ProviderManager providerManager(CustomAuthProvider customAuthProvider) {
		return new ProviderManager(customAuthProvider);
	}
}