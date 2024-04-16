package org.ecommerce.userapi.security.custom;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseConfigurer {
	default void responseSetting(HttpServletResponse response, UserErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getCode());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		Response responseBody = new Response<>(
			errorCode.getCode(), errorCode.getMessage()
		);

		response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
	}
}
