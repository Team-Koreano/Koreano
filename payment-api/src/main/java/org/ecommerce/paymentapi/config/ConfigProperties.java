package org.ecommerce.paymentapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties("toss")
public class ConfigProperties {
	private String secretKey;
}
