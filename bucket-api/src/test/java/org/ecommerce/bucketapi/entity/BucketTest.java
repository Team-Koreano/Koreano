package org.ecommerce.bucketapi.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BucketTest {

	@Test
	void 장바구니_담기() {
		// given
		final Bucket bucket = new Bucket(
				1L,
				1,
				"seller",
				101,
				3,
				LocalDate.of(2024, 5, 2)
		);
		// when
		Bucket newBucket = Bucket.ofAdd(1, "seller", 101, 3);

		// then
		assertThat(newBucket.getUserId()).isEqualTo(bucket.getUserId());
		assertThat(newBucket.getSeller()).isEqualTo(bucket.getSeller());
		assertThat(newBucket.getProductId()).isEqualTo(bucket.getProductId());
		assertThat(newBucket.getQuantity()).isEqualTo(bucket.getQuantity());
	}

	@Test
	void 장바구니_상품_수정() {
		// given
		final Integer newQuantity = 777;
		final Bucket bucket = new Bucket(
				1L,
				1,
				"seller",
				101,
				3,
				LocalDate.of(2024, 4, 14)
		);

		// when
		bucket.modifyQuantity(newQuantity);

		// then
		assertThat(bucket.getQuantity()).isEqualTo(newQuantity);
	}
}
