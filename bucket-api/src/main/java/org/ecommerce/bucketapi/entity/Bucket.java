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

	@Column(nullable = false)
	private Integer userId;

	@Column(nullable = false)
	private String seller;

	@Column(nullable = false)
	private Integer productId;

	@Column(nullable = false)
	private Integer quantity;

	@CreationTimestamp
	@Column
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

	// TODO : 상품 상세옵션 변경 로직 추가
	public void modifyQuantity(final Integer quantity) {
		this.quantity = quantity;
	}
}
