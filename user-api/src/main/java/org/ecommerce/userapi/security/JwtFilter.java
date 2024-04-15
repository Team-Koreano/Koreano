package org.ecommerce.userapi.security;

import java.io.IOException;

import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final ProviderManager providerManager;
	private final JwtUtils jwtUtils;
	private final RedisUtils redisUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String jwt = jwtUtils.resolveToken(request);

		if (jwt != null) {
			boolean isLogout = redisUtils.hasKey(jwtUtils.getAccessTokenKey(jwtUtils.getUserId(jwt),jwtUtils.getAuthority(jwt)));
			if (isLogout) {
				// Authentication 검증 전
				Authentication beforeAuthentication = jwtUtils.parseAuthentication(jwt);
				// Authentication 검증 후
				Authentication afterAuthenticate = providerManager.authenticate(beforeAuthentication);
				// Context 에 저장
				SecurityContextHolder.getContext().setAuthentication(afterAuthenticate);
			}else {
				response.getWriter().write("please ReLogin");
				return;
			}
		}
		doFilter(request, response, filterChain);
	}
}
