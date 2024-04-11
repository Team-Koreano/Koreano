package org.ecommerce.product.entity;

import java.time.LocalDateTime;

import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductCategory category;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private SellerRep sellerRep;

	@Column(name = "favorite_count")
	private Integer favoriteCount;

	@Column(name = "is_decaf", nullable = false)
	private Boolean isDecaf;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "bean_type")
	@Enumerated(EnumType.STRING)
	private Bean bean;

	@Column(name = "acidity")
	@Enumerated(EnumType.STRING)
	private Acidity acidity;

	@Column(name = "information")
	private String information;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	@CreationTimestamp
	@Column(name = "create_datetime", nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@UpdateTimestamp
	@Column(name = "update_datetime")
	private LocalDateTime updateDatetime;

}
