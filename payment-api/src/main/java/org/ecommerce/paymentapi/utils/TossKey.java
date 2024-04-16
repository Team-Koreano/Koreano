package org.ecommerce.paymentapi.utils;

import java.util.Base64;

import org.ecommerce.paymentapi.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TossKey {

	private final ConfigProperties properties;
	@Getter
	private final String authorizationKey;

	@Autowired
	public TossKey(ConfigProperties properties) {
		this.properties = properties;
		authorizationKey = createAuthorizationKey();
	}

	private String createAuthorizationKey(){
		final String encode = Base64.getEncoder().encodeToString((properties.getSecretKey() + ":").getBytes());
		final String finalKey = "Basic " + encode;
		return finalKey;
	}

}
