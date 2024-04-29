package org.ecommerce.productsearchapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"org.ecommerce.product"})
@EnableJpaRepositories(basePackages = {"org.ecommerce.productsearchapi.repository"})
public class ScanConfig {
}
