package org.ecommerce.bucketapi.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.google.common.annotations.VisibleForTesting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bucket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@VisibleForTesting
	public Bucket(
			final Long id,
			final Integer userId,
			final String seller,
			final Integer productId,
			final Integer quantity,
			final LocalDate createDate
	) {
		this.id = id;
		this.userId = userId;
		this.seller = seller;
		this.productId = productId;
		this.quantity = quantity;
		this.createDate = createDate;
	}

	public static Bucket ofAdd(
			final Integer userId,
			final String seller,
			final Integer productId,
			final Integer quantity
	) {
		return new Bucket(
				null,
				userId,
				seller,
				productId,
				quantity,
				null
		);
	}

	// TODO : 상품 상세옵션 변경 로직 추가
	public void modifyQuantity(final Integer quantity) {
		this.quantity = quantity;
	}
}
