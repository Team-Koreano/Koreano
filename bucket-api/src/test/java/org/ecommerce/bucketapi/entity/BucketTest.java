package org.ecommerce.bucketapi.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.ecommerce.bucketapi.dto.BucketDto;
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
		assertThat(bucket.getUserId()).isEqualTo(1);
		assertThat(bucket.getSeller()).isEqualTo("seller");
		assertThat(bucket.getProductId()).isEqualTo(101);
		assertThat(bucket.getQuantity()).isEqualTo(3);
	}

	@Test
	void 장바구니_상품_수정() {
		// given
		final BucketDto.Request.Update bucketUpdateRequest = new BucketDto.Request.Update(777);

		// when
		BUCKET.update(bucketUpdateRequest);

		// then
		assertThat(BUCKET.getQuantity()).isEqualTo(777);
	}
}
