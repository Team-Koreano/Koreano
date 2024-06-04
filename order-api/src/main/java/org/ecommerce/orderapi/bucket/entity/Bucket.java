package org.ecommerce.orderapi.bucket.entity;

import static org.ecommerce.orderapi.bucket.exception.BucketErrorCode.*;
import static org.ecommerce.orderapi.bucket.util.BucketPolicyConstants.*;

import java.time.LocalDate;

import org.ecommerce.common.error.CustomException;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bucket", indexes = {
		@Index(name = "idx_bucket_user_id", columnList = "userId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

	public void modifyQuantity(final Integer newQuantity) {
		if (newQuantity > MAXIMUM_PRODUCT_QUANTITY) {
			throw new CustomException(TOO_MANY_QUANTITY_IN_BUCKET);
		}
		this.quantity = newQuantity;
	}
}
