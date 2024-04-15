package org.ecommerce.userapi.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityKeyConfig {

	@Bean
	public SecretKey secretKey(@Value("${jwt.secret}") String jwtSecretKey) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] sha256secretKey = digest.digest(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
		return new SecretKeySpec(sha256secretKey,"HmacSHA256");
	}
}
