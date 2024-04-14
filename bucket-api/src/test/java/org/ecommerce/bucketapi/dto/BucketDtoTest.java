package org.ecommerce.bucketapi.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.ecommerce.bucketapi.entity.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketDtoTest {

	private static final LocalDate CREATE_DATE = LocalDate.now();
	private static final Bucket BUCKET = new Bucket(
		1L,
		1,
		"seller",
		101,
		3,
		CREATE_DATE
	);

	@Test
	void 장바구니_응답() {
		// given
		// when
		final BucketDto.Response actual = BucketDto.Response.of(BUCKET);

		// then
		assertThat(actual.id()).isEqualTo(BUCKET.getId());
		assertThat(actual.userId()).isEqualTo(BUCKET.getUserId());
		assertThat(actual.seller()).isEqualTo(BUCKET.getSeller());
		assertThat(actual.productId()).isEqualTo(BUCKET.getProductId());
		assertThat(actual.quantity()).isEqualTo(BUCKET.getQuantity());
		assertThat(actual.createDate()).isEqualTo(BUCKET.getCreateDate());
	}
}
