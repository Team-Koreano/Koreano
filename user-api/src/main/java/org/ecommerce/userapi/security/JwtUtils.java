package org.ecommerce.userapi.security;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.entity.Member;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtUtils {
	private static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
	private static final String PREFIX = "Bearer ";

	private final RedisUtils redisUtils;

	private final SecretKey secretKey;

	public static String prefix(String jwt) {
		if (jwt.startsWith(PREFIX))
			return jwt;
		return PREFIX + jwt;
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader(ACCESS_TOKEN_HEADER);
	}

	public String cutPreFix(String jwt) {
		if (!jwt.startsWith(PREFIX))
			return jwt;
		return jwt.substring(PREFIX.length());
	}

	public String createToken(Integer userId, String email, long lifeCycle, SecretKey secretKey,
		Set<String> authorization) {
		Date now = new Date();
		return Jwts.builder()
			.claim("userId", userId)
			.claim("email", email)
			.claim("authorization", authorization)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + lifeCycle * 1000))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Integer getUserId(String jwt) {
		String cuttedJwt = cutPreFix(jwt);
		Claims claims = parseClaims(cuttedJwt);
		return claims.get("userId", Integer.class);
	}

	public String createTokens(Member member, Set<String> authorization) {
		// 1000 *60 *60
		final long oneHour = 3_600;
		final long twoWeeks = oneHour * 24 * 14;

		final String accessToken = createToken(member.getId(), member.getEmail(), oneHour, secretKey, authorization);
		final String refreshToken = createToken(member.getId(), member.getEmail(), twoWeeks, secretKey, authorization);

		String accessTokenKey = getAccessTokenKey(member.getId(),getAuthority(accessToken));
		redisUtils.setData(accessTokenKey, accessToken, oneHour, TimeUnit.SECONDS);


		String refreshTokenKey = getRefreshTokenKey(member.getId(), getAuthority(refreshToken));
		redisUtils.setData(refreshTokenKey, refreshToken, twoWeeks, TimeUnit.SECONDS);

		return accessToken;
	}

	//TODO : 유효성 검증,
	public Claims parseClaims(String jwt) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException e) {
			throw new CustomException(UserErrorCode.EXPIRED_JWT);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			throw new CustomException(UserErrorCode.INVALID_SIGNATURE_JWT);
		} catch (IllegalArgumentException e) {
			throw new CustomException(UserErrorCode.EMPTY_JWT);
		}
	}

	public String getRefreshTokenKey(Integer id, String auth) {
		return "refresh_token" + id + auth;
	}

	public String getAccessTokenKey(Integer id, String auth) {
		return "access_token" + id + auth;
	}

	public UsernamePasswordAuthenticationToken parseAuthentication(String jwt) {
		String cutPreFix = cutPreFix(jwt);
		Claims claims = parseClaims(cutPreFix);
		String email = claims.get("email", String.class);
		List<?> authorities = claims.get("authorization", List.class);
		if (authorities == null)
			throw new CustomException(UserErrorCode.INVALID_SIGNATURE_JWT);
		Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
			.map(String::valueOf)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toSet());
		return new UsernamePasswordAuthenticationToken(email, jwt, grantedAuthorities);
	}

	public String getAuthority(String jwt) {
		Authentication authentication = parseAuthentication(jwt);
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}
}
