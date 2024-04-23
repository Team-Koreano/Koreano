package org.ecommerce.orderapi.entity;

import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@RedisHash(value = "stock")
public class Stock {

	@Id
	private Integer productId;

	private Integer totalStock;
	private Integer processingStock;
}
