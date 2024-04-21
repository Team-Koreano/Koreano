package org.ecommerce.bucketapi.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.service.BucketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InternalBucketController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class InternalBucketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BucketService bucketService;

	@Test
	void MS간_장바구니_조회() throws Exception {
		// given
		final List<BucketDto> bucketDtos =
				Arrays.asList(
						new BucketDto(
								1L,
								1,
								"seller1",
								101,
								3,
								LocalDate.of(2024, 4, 14)
						),
						new BucketDto(
								2L,
								1,
								"seller2",
								102,
								2,
								LocalDate.of(2024, 4, 14
								)
						)
				);
		// when
		when(bucketService.getBuckets(anyInt(), anyList())).thenReturn(bucketDtos);

		// then
		mockMvc.perform(get("/api/internal/buckets/v1/1?bucketIds=1,2"))
				.andExpect(jsonPath("$.[0].id").value(bucketDtos.get(0).getId()))
				.andExpect(jsonPath("$.[0].seller")
						.value(bucketDtos.get(0).getSeller()))
				.andExpect(jsonPath("$.[0].productId")
						.value(bucketDtos.get(0).getProductId()))
				.andExpect(jsonPath("$.[0].quantity")
						.value(bucketDtos.get(0).getQuantity()))
				.andExpect(jsonPath("$.[1].id").value(bucketDtos.get(1).getId()))
				.andExpect(jsonPath("$.[1].seller")
						.value(bucketDtos.get(1).getSeller()))
				.andExpect(jsonPath("$.[1].productId")
						.value(bucketDtos.get(1).getProductId()))
				.andExpect(jsonPath("$.[1].quantity")
						.value(bucketDtos.get(1).getQuantity()))
				.andExpect(status().isOk())
				.andDo(print());
	}

}
