package org.ecommerce.userapi.security;

import java.util.HashSet;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

	private final JwtProvider jwtProvider;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String email = (String)authentication.getPrincipal();
		String bearerToken = (String)authentication.getCredentials();

		String parseEmail = jwtProvider.getEmail(bearerToken);
		String parseRole = jwtProvider.getRoll(bearerToken);
		Integer parseId = jwtProvider.getId(bearerToken);

		AuthDetails authDetails = new AuthDetails(parseId, parseEmail, parseRole);

		SimpleGrantedAuthority parsedGrant = new SimpleGrantedAuthority(parseRole);

		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		authorities.add(parsedGrant);

		if (!authDetails.getEmail().equals(email) ||
			!authentication.getAuthorities().contains(parsedGrant)) {
			throw new CustomException(UserErrorCode.AUTHENTICATION_FAILED);
		}
		return new UsernamePasswordAuthenticationToken(authDetails, bearerToken, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
