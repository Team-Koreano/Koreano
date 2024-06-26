package org.ecommerce.common.provider;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.ecommerce.common.error.CommonErrorCode;
import org.ecommerce.common.error.CustomException;
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
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
	private static final String TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
	private static final String CLAIM_ID = "id";
	private static final String CLAIM_AUTHORIZATION = "authorization";
	private static final String PREFIX = "Bearer ";

	private static final Integer COOKIE_EXPIRE_TIME = 1_209_600;

	private final SecretKey secretKey;

	public static String prefix(String jwt) {
		if (jwt.startsWith(PREFIX))
			return jwt;
		return PREFIX + jwt;
	}

	public String createToken(Integer id, long lifeCycle, SecretKey secretKey,
		Set<String> authorization) {
		Date now = new Date();
		return Jwts.builder()
			.claim(CLAIM_ID, id)
			.claim(CLAIM_AUTHORIZATION, authorization)
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
		Integer id = claims.get(CLAIM_ID, Integer.class);
		List<?> authorities = claims.get(CLAIM_AUTHORIZATION, List.class);

		if (authorities == null)
			throw new CustomException(CommonErrorCode.INVALID_SIGNATURE_JWT);

		Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
			.map(String::valueOf)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toSet());

		return new UsernamePasswordAuthenticationToken(id, bearerToken, grantedAuthorities);
	}

	public String getRoll(String bearerToken) {
		return parseClaims(cutPreFix(bearerToken)).get(CLAIM_AUTHORIZATION, List.class).get(0).toString();
	}

	public Integer getId(String bearerToken) {
		return parseClaims(cutPreFix(bearerToken)).get(CLAIM_ID, Integer.class);
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
		return request.getHeader(TOKEN_HEADER);
	}

	public Cookie createCookie(String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(COOKIE_EXPIRE_TIME);
		return cookie;
	}
}
