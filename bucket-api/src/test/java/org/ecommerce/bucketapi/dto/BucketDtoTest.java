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
		101,
		3,
		CREATE_DATE
	);

	@Test
	void 장바구니_응답() {
		// given
		// when
		final BucketDTO.Response actual = BucketDTO.Response.of(BUCKET);

		// then
		assertThat(actual.id()).isEqualTo(1L);
		assertThat(actual.userId()).isEqualTo(1);
		assertThat(actual.productId()).isEqualTo(101);
		assertThat(actual.quantity()).isEqualTo(3);
		assertThat(actual.createDate()).isEqualTo(CREATE_DATE);
	}
}
