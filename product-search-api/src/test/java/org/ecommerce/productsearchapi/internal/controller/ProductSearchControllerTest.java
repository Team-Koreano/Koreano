package org.ecommerce.productsearchapi.internal.controller;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.internal.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
		final List<ProductDto.ImageDto> imageDtoList = List.of(
			new ProductDto.ImageDto(1, true, (short)1, TEST_DATE_TIME, TEST_DATE_TIME, "http://image1.com",
				false),
			new ProductDto.ImageDto(2, false, (short)2, TEST_DATE_TIME, TEST_DATE_TIME, "http://image2.com",
				false)
		);

		final ProductDto productDto =
			new ProductDto(
				1,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductDto.SellerRep(1, "커피천국"),
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
				"testCapacity"
			);

		String productJsonBody = objectMapper.writeValueAsString(getProduct());

		// when
		when(productSearchService.saveProduct(any(Product.class))).thenReturn(productDto);

		// // then
		// mockMvc.perform(MockMvcRequestBuilders
		// 		.post("/api/internal/product/v1")
		// 		.contentType(MediaType.APPLICATION_JSON)
		// 		.content(productJsonBody))
		// 	.andExpect(jsonPath("$.id").value(productSearchDto.getId()))
		// 	.andExpect(jsonPath("$.category").value(productSearchDto.getCategory().getTitle()))
		// 	.andExpect(jsonPath("$.price").value(productSearchDto.getPrice()))
		// 	.andExpect(jsonPath("$.stock").value(productSearchDto.getStock()))
		// 	.andExpect(jsonPath("$.sellerId").value(productSearchDto.getSellerRep().getId()))
		// 	.andExpect(jsonPath("$.sellerName").value(productSearchDto.getSellerRep().getBizName()))
		// 	.andExpect(jsonPath("$.favoriteCount").value(productSearchDto.getFavoriteCount()))
		// 	.andExpect(jsonPath("$.isDecaf").value(productSearchDto.getIsDecaf()))
		// 	.andExpect(jsonPath("$.name").value(productSearchDto.getName()))
		// 	.andExpect(jsonPath("$.bean").value(productSearchDto.getBean().getTitle()))
		// 	.andExpect(jsonPath("$.acidity").value(productSearchDto.getAcidity().getTitle()))
		// 	.andExpect(jsonPath("$.information").value(productSearchDto.getInformation()))
		// 	.andExpect(jsonPath("$.createDatetime").value(productSearchDto.getCreateDatetime().toString()))
		// 	.andExpect(jsonPath("$.thumbnailUrl").value(
		// 		ProductSearchDto.Response.SavedProduct.getThumbnailUrl(productSearchDto.getImageDtoList())))
		// 	.andExpect(status().isOk())
		// 	.andDo(print());
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
			"testSize",
			"testCapacity",
			ProductStatus.AVAILABLE,
			TEST_DATE_TIME,
			TEST_DATE_TIME,
			(short)3000,
			images
		);
	}

}
