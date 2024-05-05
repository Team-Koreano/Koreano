package org.ecommerce.orderapi.entity;

import org.ecommerce.orderapi.entity.enumerated.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Product {

	private Integer id;

	private String name;
	private Integer price;
	private String seller;
	private ProductStatus status;
}
