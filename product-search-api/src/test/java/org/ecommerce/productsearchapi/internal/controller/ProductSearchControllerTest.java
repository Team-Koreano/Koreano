package org.ecommerce.productsearchapi.internal.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;
import org.ecommerce.productsearchapi.dto.SellerRepDto;
import org.ecommerce.productsearchapi.dto.response.SaveDocumentResponse;
import org.ecommerce.productsearchapi.internal.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = ProductSearchController.class)
@ExtendWith(MockitoExtension.class)
public class ProductSearchControllerTest {

	LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2024, 4, 22, 3, 23, 1);

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private ProductSearchService productSearchService;

	@Test
	void 엘라스틱서치에_상품_정보_저장() throws Exception {
		// given
		final List<ImageDto> imageDtoList = List.of(
			new ImageDto(1, true, (short)1, TEST_DATE_TIME, TEST_DATE_TIME, "http://image1.com",
				false),
			new ImageDto(2, false, (short)2, TEST_DATE_TIME, TEST_DATE_TIME, "http://image2.com",
				false)
		);

		final ProductDtoWithImageListDto productDtoWithImageListDto =
			new ProductDtoWithImageListDto(
				1,
				ProductCategory.BEAN,
				30000,
				100,
				new SellerRepDto(1, "커피천국"),
				10,
				false,
				"[특가 EVENT]&아라비카 원두&세상에서 제일 존맛 커피",
				Bean.ARABICA,
				Acidity.MEDIUM,
				"커피천국에서만 만나볼 수 있는 특별한 커피",
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				imageDtoList,
				"http://image1.com",
				"testSize",
				"testCapacity",
				(short)1000
			);

		// when
		when(productSearchService.saveProduct(any(Product.class))).thenReturn(productDtoWithImageListDto);

		// // then
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/internal/product/v1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"{\"id\":1,\"category\":\"BEAN\",\"price\":30000,\"stock\":100,\"sellerRep\":{\"id\":1,\"bizName\":\"커피천국\"},\"favoriteCount\":10,\"isDecaf\":false,\"name\":\"[특가 EVENT]&아라비카 원두&세상에서 제일 존맛 커피\",\"bean\":\"ARABICA\",\"acidity\":\"MEDIUM\",\"information\":\"커피천국에서만 만나볼 수 있는 특별한 커피\",\"isCrush\":false,\"size\":\"testSize\",\"capacity\":\"testCapacity\",\"status\":\"AVAILABLE\",\"createDatetime\":\"2024-04-22T03:23:01\",\"updateDatetime\":\"2024-04-22T03:23:01\",\"deliveryFee\":2000,\"images\":[{\"id\":1,\"product\":null,\"imageUrl\":\"http://image1.com\",\"isThumbnail\":true,\"sequenceNumber\":1,\"isDeleted\":false,\"createDatetime\":\"2024-04-22T03:23:01\",\"updateDatetime\":\"2024-04-22T03:23:01\"},{\"id\":2,\"product\":null,\"imageUrl\":\"http://image2.com\",\"isThumbnail\":false,\"sequenceNumber\":2,\"isDeleted\":false,\"createDatetime\":\"2024-04-22T03:23:01\",\"updateDatetime\":\"2024-04-22T03:23:01\"}]}"))
			.andExpect(jsonPath("$.id").value(productDtoWithImageListDto.id()))
			.andExpect(jsonPath("$.category").value(productDtoWithImageListDto.category().getTitle()))
			.andExpect(jsonPath("$.price").value(productDtoWithImageListDto.price()))
			.andExpect(jsonPath("$.stock").value(productDtoWithImageListDto.stock()))
			.andExpect(jsonPath("$.sellerId").value(productDtoWithImageListDto.sellerRep().id()))
			.andExpect(jsonPath("$.sellerName").value(productDtoWithImageListDto.sellerRep().bizName()))
			.andExpect(jsonPath("$.favoriteCount").value(productDtoWithImageListDto.favoriteCount()))
			.andExpect(jsonPath("$.isDecaf").value(productDtoWithImageListDto.isDecaf()))
			.andExpect(jsonPath("$.name").value(productDtoWithImageListDto.name()))
			.andExpect(jsonPath("$.bean").value(productDtoWithImageListDto.bean().getTitle()))
			.andExpect(jsonPath("$.acidity").value(productDtoWithImageListDto.acidity().getTitle()))
			.andExpect(jsonPath("$.information").value(productDtoWithImageListDto.information()))
			.andExpect(jsonPath("$.createDatetime").value(productDtoWithImageListDto.createDatetime().toString()))
			.andExpect(jsonPath("$.thumbnailUrl").value(
				SaveDocumentResponse.getThumbnailUrl(productDtoWithImageListDto.imageDtoList())))
			.andExpect(status().isOk())
			.andDo(print());
	}

}
