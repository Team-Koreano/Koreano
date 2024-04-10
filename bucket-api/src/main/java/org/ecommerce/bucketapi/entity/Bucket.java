package org.ecommerce.bucketapi.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bucket")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Bucket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "seller")
	private String seller;

	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "quantity")
	private Integer quantity;

	@CreationTimestamp
	@Column(name = "create_date")
	private LocalDate createDate;

	public static Bucket ofAdd(
		final Integer userId,
		final String seller,
		final Integer productId,
		final Integer quantity
	) {
		Bucket bucket = new Bucket();
		bucket.userId = userId;
		bucket.seller = seller;
		bucket.productId = productId;
		bucket.quantity = quantity;
		return bucket;
	}
}
