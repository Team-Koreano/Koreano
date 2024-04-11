package org.ecommerce.bucketapi.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.service.BucketService;
import org.ecommerce.common.vo.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
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

	private List<BucketDto.Response> createTestBucketsResponse() {
		List<BucketDto.Response> bucketResponse = new ArrayList<>();
		bucketResponse.add(new BucketDto.Response(1L, 1, "seller1", 101, 3, LocalDate.now()));
		bucketResponse.add(new BucketDto.Response(2L, 2, "seller2", 102, 2, LocalDate.now()));
		bucketResponse.add(new BucketDto.Response(3L, 1, "seller3", 103, 1, LocalDate.now()));

		return bucketResponse;
	}

	private BucketDto.Response createTestBucketResponse() {
		return new BucketDto.Response(1L, 1, "seller", 101, 3, LocalDate.now());
	}

	private ResultActions performGetRequest() throws Exception {
		return mockMvc.perform(get("/buckets"));
	}

	private ResultActions performPostRequest(final BucketDto.Request.Add bucketAddRequest) throws Exception {
		return mockMvc.perform(post("/buckets")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(bucketAddRequest)));
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

		final List<BucketDto.Response> bucketResponse = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<List<BucketDto.Response>>>() {
			}
		).result();

		assertThat(bucketResponse).usingRecursiveComparison()
			.isEqualTo(createTestBucketsResponse());
	}

	@Test
	void 장바구니_담기() throws Exception {
		// given
		when(bucketService.addBucket(anyInt(), any(BucketDto.Request.Add.class)))
			.thenReturn(createTestBucketResponse());
		final BucketDto.Request.Add bucketAddRequest = new BucketDto.Request.Add(
			"seller",
			101,
			3
		);

		// when
		final ResultActions resultActions = performPostRequest(bucketAddRequest);

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		final BucketDto.Response bucketResponse = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<BucketDto.Response>>() {
			}
		).result();

		assertThat(bucketResponse).isEqualTo(createTestBucketResponse());
	}

	@Test
	void 판매자_입력_없이_장바구니_담기() throws Exception{
		// given
		final BucketDto.Request.Add bucketAddRequest = new BucketDto.Request.Add(
			null,
			101,
			3
		);

		// when
		final ResultActions resultActions = performPostRequest(bucketAddRequest);

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.result").value("판매자를 입력해 주세요."));
	}

	@Test
	void 상품_수량_선택_없이_장바구니_담기() throws Exception{
		// given
		final BucketDto.Request.Add bucketAddRequest = new BucketDto.Request.Add(
			"seller",
			101,
			null
		);

		// when
		final ResultActions resultActions = performPostRequest(bucketAddRequest);

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.result").value("상품 수량을 입력해 주세요."));
	}
}
