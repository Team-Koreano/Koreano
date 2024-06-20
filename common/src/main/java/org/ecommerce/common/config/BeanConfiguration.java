package org.ecommerce.common.config;

import org.ecommerce.common.security.CustomAuthProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfiguration {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ProviderManager providerManager(@Qualifier("customAuthProvider") CustomAuthProvider authProvider) {
		return new ProviderManager(authProvider);
	}
}