package org.ecommerce.userapi.security;

import java.io.IOException;

import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.provider.RedisProvider;
import org.ecommerce.userapi.security.custom.ResponseConfigurer;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
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

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter implements ResponseConfigurer {

	private final ProviderManager providerManager;
	private final JwtProvider jwtProvider;
	private final RedisProvider redisProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String bearerToken = jwtProvider.resolveToken(request);

		try {
			if (bearerToken != null) {
				boolean isLogin = redisProvider.hasKey(
					jwtProvider.getAccessTokenKey(jwtProvider.getId(bearerToken), jwtProvider.getRoll(bearerToken)));
				if (isLogin) {
					// Authentication 검증 전
					Authentication beforeAuthentication = jwtProvider.parseAuthentication(bearerToken);
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

