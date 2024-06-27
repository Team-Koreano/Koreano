package org.ecommerce.userapi.external.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.userapi.provider.RedisProvider;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerTokenService {

	private final static long ACCESS_VALID_TIME = 3_600;

	private final static long REFRESH_VALID_TIME = ACCESS_VALID_TIME * 24 * 14;

	private final SecretKey secretKey;

	private final RedisProvider redisProvider;

	private final JwtProvider jwtProvider;

	public String createSellerTokens(Integer sellerId, Set<String> authorization,
		HttpServletResponse response) {

		final String accessToken = jwtProvider.createToken(
			sellerId, ACCESS_VALID_TIME, secretKey, authorization
		);

		final String refreshToken = jwtProvider.createToken(
			sellerId, REFRESH_VALID_TIME, secretKey, authorization
		);

		String accessTokenKey = jwtProvider.getAccessTokenKey(sellerId, jwtProvider.getRoll(accessToken));
		redisProvider.setData(accessTokenKey, accessToken, ACCESS_VALID_TIME, TimeUnit.SECONDS);

		String refreshTokenKey = jwtProvider.getRefreshTokenKey(sellerId, jwtProvider.getRoll(refreshToken));
		redisProvider.setData(refreshTokenKey, refreshToken, REFRESH_VALID_TIME, TimeUnit.SECONDS);

		response.addCookie(jwtProvider.createCookie(refreshToken));

		return accessToken;
	}

	public void removeTokens(String accessTokenKey, String refreshTokenKey) {
		redisProvider.deleteData(accessTokenKey);
		redisProvider.deleteData(refreshTokenKey);
	}
}
