package org.ecommerce.userapi.security.custom;

import java.util.Objects;

import org.ecommerce.userapi.provider.JwtProvider;
import org.ecommerce.userapi.security.AuthDetails;
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

	private final JwtProvider jwtProvider;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class);
	}

	@Override
	public AuthDetails resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String bearerToken = jwtProvider.resolveToken(
			Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)));
		if (bearerToken != null) {
			Integer userId = jwtProvider.getId(bearerToken);
			String role = jwtProvider.getRoll(bearerToken);
			return new AuthDetails(userId, role);
		}
		return new AuthDetails(null, null);
	}
}