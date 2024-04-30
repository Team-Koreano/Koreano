package org.ecommerce.userapi.security.custom;

import java.util.Objects;

import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtUtils jwtUtils;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class);
	}

	@Override
	public AuthDetails resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String bearerToken = jwtUtils.resolveToken(
			Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)));
		if (bearerToken != null) {
			Integer userId = jwtUtils.getId(bearerToken);
			String email = jwtUtils.getEmail(bearerToken);
			String role = jwtUtils.getRoll(bearerToken);
			return new AuthDetails(userId, email, role);
		}
		return new AuthDetails(null, null, null);
	}
}