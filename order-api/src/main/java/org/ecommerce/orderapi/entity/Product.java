package org.ecommerce.orderapi.entity;

import org.ecommerce.orderapi.entity.type.ProductStatus;
import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@RedisHash(value = "product")
public class Product {

	@Id
	private Integer id;

	private String name;
	private Integer price;
	private String seller;
	private ProductStatus status;
}
