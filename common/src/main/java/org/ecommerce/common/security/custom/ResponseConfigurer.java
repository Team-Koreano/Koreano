package org.ecommerce.common.security.custom;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.ecommerce.common.error.CommonErrorCode;
import org.ecommerce.common.vo.Response;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseConfigurer {
	default void responseSetting(HttpServletResponse response, CommonErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getCode());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		Response<String> responseBody = new Response<>(
			errorCode.getCode(), errorCode.getMessage()
		);

		response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
	}
}
