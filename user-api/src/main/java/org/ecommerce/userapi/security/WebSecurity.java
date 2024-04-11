package org.ecommerce.userapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurity {

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable);
		//        http.csrf(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests((authz) -> authz
				.requestMatchers("/**").authenticated()
			)
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

		return http.build();
	}
}
