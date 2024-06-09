package org.ecommerce.productapi.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.s3.AmazonS3Client;

@TestConfiguration
public class MockS3Config {

	@Bean
	public AmazonS3Client s3Mock() {
		return Mockito.mock(AmazonS3Client.class);
	}
}
