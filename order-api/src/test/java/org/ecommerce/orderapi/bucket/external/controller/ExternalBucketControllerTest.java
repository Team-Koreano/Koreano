package org.ecommerce.orderapi.bucket.external.controller;

import static org.ecommerce.orderapi.bucket.exception.ErrorMessage.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.ecommerce.orderapi.ControllerTest;
import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.service.BucketDomainService;
import org.ecommerce.orderapi.bucket.service.BucketReadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BucketController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class ExternalBucketControllerTest extends ControllerTest {

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
		final int pageNumber = 1;
		final int pageSize = 10;
		final long total = 2L;
		final Page<BucketDto> bucketDtos = new PageImpl<>(
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
				),
				PageRequest.of(pageNumber, pageSize),
				total
		);
		when(bucketReadService.getAllBuckets(anyInt(), anyInt(), anyInt()))
				.thenReturn(bucketDtos);

		// when
		// then
		BucketDto bucketDto = bucketDtos.getContent().get(0);
		mockMvc.perform(get("/api/external/buckets/v1"))
				.andDo(print())
				.andExpect(jsonPath("$.result.content[0].id").value(bucketDto.id()))
				.andExpect(jsonPath("$.result.content[0].seller")
						.value(bucketDto.seller()))
				.andExpect(jsonPath("$.result.content[0].productId")
						.value(bucketDto.productId()))
				.andExpect(jsonPath("$.result.content[0].quantity")
						.value(bucketDto.quantity()))
				.andExpect(jsonPath("$.result.content[0].id").value(bucketDto.id()))
				.andExpect(jsonPath("$.result.content[0].seller")
						.value(bucketDto.seller()))
				.andExpect(jsonPath("$.result.content[0].productId")
						.value(bucketDto.productId()))
				.andExpect(jsonPath("$.result.content[0].quantity")
						.value(bucketDto.quantity()))
				.andExpect(status().isOk());
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
		final Integer newQuantity = 7;
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

	@Test
	void 장바구니_삭제() throws Exception {
		// given
		BucketDto bucketDto = new BucketDto(
				1L,
				1,
				"판매자 이름",
				101,
				10,
				LocalDate.of(2024, 5, 28)
		);
		when(bucketDomainService.deleteBucket(anyInt(), anyLong())).thenReturn(bucketDto);

		// when
		// then
		mockMvc.perform(delete("/api/external/buckets/v1/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(bucketDto.id()))
				.andExpect(jsonPath("$.result.userId").value(bucketDto.userId()))
				.andExpect(jsonPath("$.result.seller").value(bucketDto.seller()))
				.andExpect(jsonPath("$.result.productId").value(bucketDto.productId()))
				.andExpect(jsonPath("$.result.quantity").value(bucketDto.quantity()))
				.andDo(print());
	}

}
