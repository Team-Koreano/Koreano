package org.ecommerce.bucketapi.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BucketTest {

	private static final LocalDate CREATE_DATE = LocalDate.now();

	private static final Bucket BUCKET = new Bucket(1L, 1, "seller", 101, 3, CREATE_DATE);

	@Test
	void 장바구니_담기() {
		// given
		// when
		Bucket bucket = Bucket.ofAdd(1, "seller", 101, 3);

		// then
		assertThat(bucket.getUserId()).isEqualTo(BUCKET.getUserId());
		assertThat(bucket.getSeller()).isEqualTo(BUCKET.getSeller());
		assertThat(bucket.getProductId()).isEqualTo(BUCKET.getProductId());
		assertThat(bucket.getQuantity()).isEqualTo(BUCKET.getQuantity());
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
