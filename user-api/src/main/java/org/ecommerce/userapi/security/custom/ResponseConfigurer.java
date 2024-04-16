package org.ecommerce.userapi.security.custom;

import java.io.IOException;
import java.util.Map;

import org.ecommerce.userapi.exception.UserErrorCode;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseConfigurer {
	default void responseSetting(HttpServletResponse response, UserErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getCode());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> responseBody = Map.of(
			"status", errorCode.getCode(),
			"result", errorCode.getMessage()
		);

		response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
	}
}
