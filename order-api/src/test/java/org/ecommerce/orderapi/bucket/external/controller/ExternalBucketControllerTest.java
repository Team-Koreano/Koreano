package org.ecommerce.orderapi.bucket.external.controller;

import static org.ecommerce.orderapi.bucket.exception.ErrorMessage.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.service.BucketDomainService;
import org.ecommerce.orderapi.bucket.service.BucketReadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BucketController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ExternalBucketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BucketDomainService bucketDomainService;

	@MockBean
	private BucketReadService bucketReadService;

	@Test
	void 장바구니_조회() throws Exception {
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
		when(bucketReadService.getAllBuckets(anyInt())).thenReturn(bucketDtos);

		// when
		// then
		mockMvc.perform(get("/api/external/buckets/v1"))
				.andExpect(jsonPath("$.result[0].id").value(bucketDtos.get(0).id()))
				.andExpect(jsonPath("$.result[0].seller")
						.value(bucketDtos.get(0).seller()))
				.andExpect(jsonPath("$.result[0].productId")
						.value(bucketDtos.get(0).productId()))
				.andExpect(jsonPath("$.result[0].quantity")
						.value(bucketDtos.get(0).quantity()))
				.andExpect(jsonPath("$.result[1].id").value(bucketDtos.get(1).id()))
				.andExpect(jsonPath("$.result[1].seller")
						.value(bucketDtos.get(1).seller()))
				.andExpect(jsonPath("$.result[1].productId")
						.value(bucketDtos.get(1).productId()))
				.andExpect(jsonPath("$.result[1].quantity")
						.value(bucketDtos.get(1).quantity()))
				.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	void 장바구니_담기() throws Exception {
		// given
		final BucketDto bucketDto = new BucketDto(
				1L, 1, "seller1", 101, 3, LocalDate.of(2024, 4, 14));
		when(bucketDomainService.addBucket(anyInt(), any(AddBucketRequest.class)))
				.thenReturn(bucketDto);

		// when
		// then
		mockMvc.perform(post("/api/external/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new AddBucketRequest(
										"seller",
										101,
										3
								)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.userId").value(bucketDto.userId()))
				.andExpect(jsonPath("$.result.seller").value(bucketDto.seller()))
				.andExpect(jsonPath("$.result.productId").value(bucketDto.productId()))
				.andExpect(jsonPath("$.result.quantity").value(bucketDto.quantity()))
				.andDo(print());
	}

	@Test
	void 판매자_입력_없이_장바구니_담기() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(post("/api/external/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new AddBucketRequest(
										null,
										101,
										3
								)
						)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.result").value(ERROR_SELLER_REQUIRED))
				.andDo(print());
	}

	@Test
	void 상품_수량_선택_없이_장바구니_담기() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(post("/api/external/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new AddBucketRequest(
										"seller",
										101,
										null
								)
						)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.result").value(ERROR_QUANTITY_REQUIRED));
	}

	@Test
	void 장바구니_상품_수정() throws Exception {
		// given
		final Integer newQuantity = 777;
		final BucketDto bucketDto = new BucketDto(
				1L,
				1,
				"seller1",
				101,
				3,
				LocalDate.of(2024, 4, 14)
		);
		given(bucketDomainService.modifyBucket(anyInt(), anyLong(),
				any(ModifyBucketRequest.class)))
				.willReturn(bucketDto);

		// when
		// then
		mockMvc.perform(put("/api/external/buckets/v1/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new ModifyBucketRequest(newQuantity)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.userId").value(bucketDto.userId()))
				.andExpect(jsonPath("$.result.seller").value(bucketDto.seller()))
				.andExpect(jsonPath("$.result.productId").value(bucketDto.productId()))
				.andExpect(jsonPath("$.result.quantity").value(bucketDto.quantity()))
				.andDo(print());
	}

	@Test
	void 상품_수량_선택_없이_장바구니_수정() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(put("/api/external/buckets/v1/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new ModifyBucketRequest(null)
						)))
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.result").value(ERROR_QUANTITY_REQUIRED))
				.andDo(print());
	}
}
