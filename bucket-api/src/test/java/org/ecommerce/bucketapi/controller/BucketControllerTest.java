package org.ecommerce.bucketapi.controller;

import static org.mockito.BDDMockito.*;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

	@Test
	void 장바구니_조회() throws Exception {
		// given
		List<BucketDto> bucketDtos =
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
		when(bucketService.getAllBuckets(anyInt())).thenReturn(bucketDtos);

		// when
		// then
		mockMvc.perform(get("/api/buckets/v1"))
				.andExpect(jsonPath("$.result[0].userId").value(1))
				.andExpect(jsonPath("$.result[0].seller").value("seller1"))
				.andExpect(jsonPath("$.result[0].productId").value(101))
				.andExpect(jsonPath("$.result[0].quantity").value(3))
				.andExpect(jsonPath("$.result[1].id").value(2))
				.andExpect(jsonPath("$.result[1].seller").value("seller2"))
				.andExpect(jsonPath("$.result[1].productId").value(102))
				.andExpect(jsonPath("$.result[1].quantity").value(2))
				.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	void 장바구니_담기() throws Exception {
		// given
		BucketDto bucketDto = new BucketDto(
				1L, 1, "seller1", 101, 3, LocalDate.of(2024, 4, 14));
		when(bucketService.addBucket(anyInt(), any(BucketDto.Request.Add.class)))
				.thenReturn(bucketDto);

		// when
		// then
		mockMvc.perform(post("/api/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new BucketDto.Request.Add(
										"seller",
										101,
										3
								)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.userId").value(1))
				.andExpect(jsonPath("$.result.seller").value("seller1"))
				.andExpect(jsonPath("$.result.productId").value(101))
				.andExpect(jsonPath("$.result.quantity").value(3))
				.andDo(print());
	}

	@Test
	void 판매자_입력_없이_장바구니_담기() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(post("/api/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new BucketDto.Request.Add(
										null,
										101,
										3
								)
						)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("400"))
				.andExpect(jsonPath("$.result").value("판매자를 입력해 주세요."))
				.andDo(print());
	}

	@Test
	void 상품_수량_선택_없이_장바구니_담기() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(post("/api/buckets/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new BucketDto.Request.Add(
										"seller",
										101,
										null
								)
						)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("400"))
				.andExpect(jsonPath("$.result").value("상품 수량을 입력해 주세요."));
	}

	@Test
	void 장바구니_상품_수정() throws Exception {
		// given
		BucketDto bucketDto = new BucketDto(
				1L,
				1,
				"seller1",
				101,
				3,
				LocalDate.of(2024, 4, 14)
		);
		given(bucketService.updateBucket(anyLong(), any(BucketDto.Request.Update.class)))
				.willReturn(bucketDto);
		doNothing().when(bucketService).validateBucketByUser(anyInt(), anyLong());

		// when
		// then
		mockMvc.perform(put("/api/buckets/v1/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new BucketDto.Request.Update(777)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.userId").value(1))
				.andExpect(jsonPath("$.result.seller").value("seller1"))
				.andExpect(jsonPath("$.result.productId").value(101))
				.andExpect(jsonPath("$.result.quantity").value(3))
				.andDo(print());
	}

	@Test
	void 상품_수량_선택_없이_장바구니_수정() throws Exception {
		// given
		// when
		// then
		mockMvc.perform(put("/api/buckets/v1/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new BucketDto.Request.Update(null)
						)))
				.andExpect(jsonPath("$.status").value("400"))
				.andExpect(jsonPath("$.result").value("상품 수량을 입력해 주세요."))
				.andDo(print());
	}
}
