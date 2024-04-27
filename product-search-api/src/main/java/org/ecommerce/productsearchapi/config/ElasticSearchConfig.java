package org.ecommerce.productsearchapi.config;

import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@EnableElasticsearchRepositories(basePackageClasses = ProductElasticsearchRepository.class)
public class ElasticSearchConfig extends ElasticsearchConfiguration {

	@Value("${spring.elasticsearch.username}")
	private String username;

	@Value("${spring.elasticsearch.password}")
	private String password;

	//TODO 추후 url yml 이나 환경변수로 관리 예정

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
				.connectedTo("localhost:9200")
				.withBasicAuth(username, password)
				.build();
	}
}
