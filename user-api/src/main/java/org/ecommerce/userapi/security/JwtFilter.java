package org.ecommerce.userapi.security;

import java.io.IOException;

import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.security.custom.ResponseConfigurer;
import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter implements ResponseConfigurer {

	private final ProviderManager providerManager;
	private final JwtUtils jwtUtils;
	private final RedisUtils redisUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String jwt = jwtUtils.resolveToken(request);

		try {
			if (jwt != null) {
				boolean isLogin = redisUtils.hasKey(
					jwtUtils.getAccessTokenKey(jwtUtils.getUserId(jwt), jwtUtils.getAuthority(jwt)));
				if (isLogin) {
					// Authentication 검증 전
					Authentication beforeAuthentication = jwtUtils.parseAuthentication(jwt);
					// Authentication 검증 후
					Authentication afterAuthenticate = providerManager.authenticate(beforeAuthentication);
					// Context 에 저장
					SecurityContextHolder.getContext().setAuthentication(afterAuthenticate);
				}
			}
		} catch (ExpiredJwtException e) {
			responseSetting(response, UserErrorCode.EXPIRED_JWT);
			return;
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			responseSetting(response, UserErrorCode.INVALID_SIGNATURE_JWT);
			return;
		} catch (IllegalArgumentException e) {
			responseSetting(response, UserErrorCode.EMPTY_JWT);
			return;
		}
		doFilter(request, response, filterChain);
	}
}

