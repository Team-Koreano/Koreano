package org.ecommerce.productsearchapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "content")
	private String content;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserRep userRep;

	@Column(name = "star_point", nullable = false)
	private Double starPoint;

	@Column(name = "is_deleted")
	private Boolean isDeleted;


}
