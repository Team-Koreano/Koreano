package org.ecommerce.bucketapi.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDTO;
import org.ecommerce.bucketapi.service.BucketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BucketController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class BucketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BucketService bucketService;

	private List<BucketDTO.Response> createTestBucketsResponse() {
		List<BucketDTO.Response> bucketResponse = new ArrayList<>();
		bucketResponse.add(new BucketDTO.Response(1L, 1, 101, 3, LocalDate.now()));
		bucketResponse.add(new BucketDTO.Response(2L, 2, 102, 2, LocalDate.now()));
		bucketResponse.add(new BucketDTO.Response(3L, 1, 103, 1, LocalDate.now()));

		return bucketResponse;
	}

	private ResultActions performGetRequest() throws Exception {
		return mockMvc.perform(get("/buckets"));
	}

	@Test
	void 장바구니_조회() throws Exception {
		// given
		when(bucketService.getAllBuckets(anyInt()))
			.thenReturn(createTestBucketsResponse());

		// when
		final ResultActions resultActions = performGetRequest();

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		final List<BucketDTO.Response> bucketResponse = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<List<BucketDTO.Response>>() {
			}
		);

		assertThat(bucketResponse).usingRecursiveComparison()
			.isEqualTo(createTestBucketsResponse());
	}
}
