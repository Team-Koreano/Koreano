package org.ecommerce.common.security;

import java.util.HashSet;
import java.util.Set;

import org.ecommerce.common.error.CommonErrorCode;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.common.provider.JwtProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

	private final JwtProvider jwtProvider;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String bearerToken = (String)authentication.getCredentials();

		String parseRole = jwtProvider.getRoll(bearerToken);
		Integer parseId = jwtProvider.getId(bearerToken);

		AuthDetails authDetails = new AuthDetails(parseId, parseRole);

		SimpleGrantedAuthority parsedGrant = new SimpleGrantedAuthority(parseRole);

		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		authorities.add(parsedGrant);

		if (!authDetails.getId().equals(authentication.getPrincipal()) ||
			!authentication.getAuthorities().contains(parsedGrant)) {
			throw new CustomException(CommonErrorCode.AUTHENTICATION_FAILED);
		}
		return new UsernamePasswordAuthenticationToken(authDetails, bearerToken, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
