package org.ecommerce.userapi.external.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.userapi.provider.RedisProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerTokenService {

	@Value("${jwt.valid.access}")
	private long ONE_HOUR;

	@Value("${jwt.valid.refresh}")
	private long TWO_WEEKS;

	private final SecretKey secretKey;

	private final RedisProvider redisProvider;

	private final JwtProvider jwtProvider;

	public String createSellerTokens(Integer sellerId, Set<String> authorization,
		HttpServletResponse response) {

		final String accessToken = jwtProvider.createToken(
			sellerId, ONE_HOUR, secretKey, authorization
		);

		final String refreshToken = jwtProvider.createToken(
			sellerId, TWO_WEEKS, secretKey, authorization
		);

		String accessTokenKey = jwtProvider.getAccessTokenKey(sellerId, jwtProvider.getRoll(accessToken));
		redisProvider.setData(accessTokenKey, accessToken, ONE_HOUR, TimeUnit.SECONDS);

		String refreshTokenKey = jwtProvider.getRefreshTokenKey(sellerId, jwtProvider.getRoll(refreshToken));
		redisProvider.setData(refreshTokenKey, refreshToken, TWO_WEEKS, TimeUnit.SECONDS);

		response.addCookie(jwtProvider.createCookie(refreshToken));

		return accessToken;
	}

	public void removeTokens(String accessTokenKey, String refreshTokenKey) {
		redisProvider.deleteData(accessTokenKey);
		redisProvider.deleteData(refreshTokenKey);
	}
}
