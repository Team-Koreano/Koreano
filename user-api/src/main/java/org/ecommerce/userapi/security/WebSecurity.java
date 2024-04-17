package org.ecommerce.userapi.security;

import org.ecommerce.userapi.security.custom.CustomAuthenticationEntryPoint;
import org.ecommerce.userapi.security.custom.CustomerAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurity {

	private final JwtFilter jwtFilter;

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests((authz) ->
				authz.requestMatchers("/api/sellers/v1/test").hasAnyAuthority("SELLER"))
			.authorizeHttpRequests((authz) ->
				authz.requestMatchers("/api/users/v1/test").hasAnyAuthority("USER"))
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers("/**").permitAll()
			).sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling((handle) -> handle.accessDeniedHandler(new CustomerAccessDeniedHandler()))
			.exceptionHandling((handle) -> handle.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
		http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
