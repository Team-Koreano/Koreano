package org.ecommerce.userapi.security;

import java.util.HashSet;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationProvider {

	private final AuthDetailsService authDetailsService;


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 이메일 불러오기
		String email = (String) authentication.getPrincipal();
		// accessToken 불러오기
		String accessToken = (String) authentication.getCredentials();
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		AuthDetails authDetails = authDetailsService.loadUserByUsername(email);

		for (GrantedAuthority authority : authentication.getAuthorities()){
			authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
		}
		if (!authDetails.getEmail().equals(email) ||
			authDetails.getAuthorities().size() != authorities.size() ||
			!authDetails.getAuthorities().containsAll(authorities)
		){
			throw new CustomException(UserErrorCode.UNRELIABLE_JWT);
		}
		return new UsernamePasswordAuthenticationToken(authDetails,accessToken,authDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
