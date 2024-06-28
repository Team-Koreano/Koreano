package org.ecommerce.common.security.custom;

import java.io.IOException;

import org.ecommerce.common.error.CommonErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomerAccessDeniedHandler implements AccessDeniedHandler, ResponseConfigurer {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		responseSetting(response, CommonErrorCode.INVALID_AUTHORIZATION);
	}
}
