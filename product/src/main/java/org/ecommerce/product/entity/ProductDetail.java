package org.ecommerce.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_detail")
public class ProductDetail {
	@Id
	@Column(name = "id", nullable = false)
	private Integer id;

	@JoinColumn(name = "product_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "size", length = 45)
	private String size;

	@Column(name = "is_default")
	private Boolean isDefault;


	@Column(name = "status", length = 45)
	private String status;

}