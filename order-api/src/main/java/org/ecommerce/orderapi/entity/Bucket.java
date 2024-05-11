package org.ecommerce.orderapi.entity;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class Bucket {
	private Long id;
	private Integer userId;
	private String seller;
	private Integer productId;
	private Integer quantity;
	private LocalDate createDate;
}
