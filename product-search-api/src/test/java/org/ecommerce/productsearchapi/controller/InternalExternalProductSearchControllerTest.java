package org.ecommerce.productsearchapi.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.service.ProductSearchService;
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

@WebMvcTest(value = InternalProductSearchController.class)
@ExtendWith(MockitoExtension.class)
public class InternalExternalProductSearchControllerTest {

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
		final List<ProductSearchDto.ImageDto> imageDtoList = List.of(
			new ProductSearchDto.ImageDto(1, true, (short)1, TEST_DATE_TIME, TEST_DATE_TIME, "http://image1.com", false),
			new ProductSearchDto.ImageDto(2, false, (short)2, TEST_DATE_TIME, TEST_DATE_TIME, "http://image2.com", false)
		);

		final ProductSearchDto productSearchDto =
			new ProductSearchDto(
				1,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductSearchDto.SellerRep(1, "커피천국"),
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
				"http://image1.com"
			);

		String productJsonBody = objectMapper.writeValueAsString(getProduct());


		// when
		when(productSearchService.saveProduct(any(Product.class))).thenReturn(productSearchDto);

		// then
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/internal/product-search/v1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productJsonBody))
			.andExpect(jsonPath("$.id").value(productSearchDto.getId()))
			.andExpect(jsonPath("$.category").value(productSearchDto.getCategory().getTitle()))
			.andExpect(jsonPath("$.price").value(productSearchDto.getPrice()))
			.andExpect(jsonPath("$.stock").value(productSearchDto.getStock()))
			.andExpect(jsonPath("$.sellerId").value(productSearchDto.getSellerRep().getId()))
			.andExpect(jsonPath("$.sellerName").value(productSearchDto.getSellerRep().getBizName()))
			.andExpect(jsonPath("$.favoriteCount").value(productSearchDto.getFavoriteCount()))
			.andExpect(jsonPath("$.isDecaf").value(productSearchDto.getIsDecaf()))
			.andExpect(jsonPath("$.name").value(productSearchDto.getName()))
			.andExpect(jsonPath("$.bean").value(productSearchDto.getBean().getTitle()))
			.andExpect(jsonPath("$.acidity").value(productSearchDto.getAcidity().getTitle()))
			.andExpect(jsonPath("$.information").value(productSearchDto.getInformation()))
			.andExpect(jsonPath("$.createDatetime").value(productSearchDto.getCreateDatetime().toString()))
			.andExpect(jsonPath("$.thumbnailUrl").value(ProductSearchDto.Response.SavedProduct.getThumbnailUrl(productSearchDto.getImageDtoList())))
			.andExpect(status().isOk())
			.andDo(print());
	}


	private Product getProduct() {
		final List<Image> images = List.of(
			new Image(1, null, "http://image1.com", true, (short)1, false, TEST_DATE_TIME, TEST_DATE_TIME),
			new Image(2, null, "http://image2.com", false, (short)2, false, TEST_DATE_TIME, TEST_DATE_TIME)
		);

		return new Product(
			1,
			ProductCategory.BEAN,
			30000,
			100,
			new SellerRep(1, "커피천국"),
			10,
			false,
			"[특가 EVENT]&아라비카 원두&세상에서 제일 존맛 커피",
			Bean.ARABICA,
			Acidity.MEDIUM,
			"커피천국에서만 만나볼 수 있는 특별한 커피",
			false,
			ProductStatus.AVAILABLE,
			TEST_DATE_TIME,
			TEST_DATE_TIME,
			images
		);
	}

}