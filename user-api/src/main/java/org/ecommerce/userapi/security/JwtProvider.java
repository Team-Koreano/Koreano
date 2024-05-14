package org.ecommerce.userapi.security;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.provider.RedisProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
	private static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
	private static final String PREFIX = "Bearer ";
	private static final long ONE_HOUR = 3_600;
	private static final long TWO_WEEKS = ONE_HOUR * 24 * 14;

	private final RedisProvider redisProvider;

	private final SecretKey secretKey;

	public static String prefix(String jwt) {
		if (jwt.startsWith(PREFIX))
			return jwt;
		return PREFIX + jwt;
	}

	public String createSellerTokens(Integer sellerId, Set<String> authorization,
		HttpServletResponse response) {

		final String accessToken = createToken(sellerId,
			ONE_HOUR, secretKey, authorization);
		final String refreshToken = createToken(sellerId,
			TWO_WEEKS, secretKey, authorization);

		String accessTokenKey = getAccessTokenKey(sellerId, getRoll(accessToken));
		redisProvider.setData(accessTokenKey, accessToken, ONE_HOUR, TimeUnit.SECONDS);

		String refreshTokenKey = getRefreshTokenKey(sellerId, getRoll(refreshToken));
		redisProvider.setData(refreshTokenKey, refreshToken, TWO_WEEKS, TimeUnit.SECONDS);

		response.addCookie(createCookie(refreshToken));

		return accessToken;
	}

	public String createUserTokens(Integer userId, Set<String> authorization,
		HttpServletResponse response) {

		final String accessToken = createToken(userId,
			ONE_HOUR, secretKey, authorization);
		final String refreshToken = createToken(userId,
			TWO_WEEKS, secretKey, authorization);

		String accessTokenKey = getAccessTokenKey(userId, getRoll(accessToken));
		redisProvider.setData(accessTokenKey, accessToken, ONE_HOUR, TimeUnit.SECONDS);

		String refreshTokenKey = getRefreshTokenKey(userId, getRoll(refreshToken));
		redisProvider.setData(refreshTokenKey, refreshToken, TWO_WEEKS, TimeUnit.SECONDS);

		response.addCookie(createCookie(refreshToken));

		return accessToken;
	}

	private String createToken(Integer id, long lifeCycle, SecretKey secretKey,
		Set<String> authorization) {
		Date now = new Date();
		return Jwts.builder()
			.claim("id", id)
			.claim("authorization", authorization)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + lifeCycle * 1000))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Claims parseClaims(String jwt) throws CustomException {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
				 IllegalArgumentException e) {
			throw e;
		}
	}

	public UsernamePasswordAuthenticationToken parseAuthentication(String bearerToken) {
		Claims claims = parseClaims(cutPreFix(bearerToken));
		Integer id = claims.get("id", Integer.class);
		List<?> authorities = claims.get("authorization", List.class);

		if (authorities == null)
			throw new CustomException(UserErrorCode.INVALID_SIGNATURE_JWT);

		Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
			.map(String::valueOf)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toSet());

		return new UsernamePasswordAuthenticationToken(id, bearerToken, grantedAuthorities);
	}

	public void removeTokens(String accessTokenKey, String refreshTokenKey) {
		redisProvider.deleteData(accessTokenKey);
		redisProvider.deleteData(refreshTokenKey);
	}

	public String getRoll(String bearerToken) {
		return parseClaims(cutPreFix(bearerToken)).get("authorization", List.class).get(0).toString();
	}

	public String getEmail(String bearerToken) {
		return parseClaims(cutPreFix(bearerToken)).get("email", String.class);
	}

	public Integer getId(String bearerToken) {
		return parseClaims(cutPreFix(bearerToken)).get("id", Integer.class);
	}

	public String getRefreshTokenKey(Integer id, String auth) {
		return "refresh_token" + id + auth;
	}

	public String getAccessTokenKey(Integer id, String auth) {
		return "access_token" + id + auth;
	}

	public String cutPreFix(String bearerToken) {
		if (!bearerToken.startsWith(PREFIX))
			return bearerToken;
		return bearerToken.substring(PREFIX.length());
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader(ACCESS_TOKEN_HEADER);
	}

	private Cookie createCookie(String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)TWO_WEEKS);
		return cookie;
	}
}
