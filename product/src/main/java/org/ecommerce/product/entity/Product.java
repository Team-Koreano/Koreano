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
	@Column(nullable = false)
	private Integer id;

	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductCategory category;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private Integer stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private SellerRep sellerRep;

	@Column()
	private Integer favoriteCount = 0;

	@Column(nullable = false)
	private Boolean isDecaf;

	@Column(nullable = false)
	private String name;

	@Column()
	@Enumerated(EnumType.STRING)
	private Bean bean;

	@Column()
	@Enumerated(EnumType.STRING)
	private Acidity acidity;

	@Column()
	private String information;

	@Column(nullable = false)
	private Boolean isCrush;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	public static Product ofCreate(ProductCategory category, Integer price, Integer stock, String name, Bean bean
		, Acidity acidity, String information,Boolean isCrush, Boolean isDecaf,SellerRep test) {
		Product product = new Product();
		product.category = category;
		product.price = price;
		product.stock = stock;
		product.name = name;
		product.bean = bean;
		product.acidity = acidity;
		product.information =information;
		product.isCrush = isCrush;
		product.status = ProductStatus.AVAILABLE;
		product.isDecaf = isDecaf;
		product.sellerRep = test;
		return product;
	}
}