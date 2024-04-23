package org.ecommerce.common.error;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String s, Response response) {
		if (response.status() == 400) {
			try {
				InputStream inputStream = response.body().asInputStream();
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(inputStream);

				int statusMessage = jsonNode.get("status").asInt();
				String resultMessage = jsonNode.get("result").asText();

				log.info("statusMessage : {}", statusMessage);
				log.info("resultMessage : {}", resultMessage);

				return new CustomException(
						new FeignErrorCode(statusMessage, resultMessage));
			} catch (Exception e) {
				return new Exception();
			}
		}
		return new Exception(response.reason());
	}
}
