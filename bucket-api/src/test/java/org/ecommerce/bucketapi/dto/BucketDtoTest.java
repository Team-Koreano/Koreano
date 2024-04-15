package org.ecommerce.bucketapi.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketDtoTest {

	@Test
	void 장바구니_응답() {
		// given
		// when
		final BucketDto.Response actual = BucketDto.Response.of(
				new BucketDto(
						1L,
						1,
						"seller",
						101,
						3,
						LocalDate.of(2024, 4, 14)
				)
		);

		// then
		assertThat(1L).isEqualTo(actual.id());
		assertThat(1).isEqualTo(actual.userId());
		assertThat("seller").isEqualTo(actual.seller());
		assertThat(101).isEqualTo(actual.productId());
		assertThat(3).isEqualTo(actual.quantity());
	}
}
