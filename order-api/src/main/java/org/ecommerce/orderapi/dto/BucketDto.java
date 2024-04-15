package org.ecommerce.orderapi.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BucketDto {
	private Long id;
	private Integer userId;
	private String seller;
	private Integer productId;
	private Integer quantity;
}
