package org.ecommerce.productsearchapi.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ProductSearchController.class)
@ExtendWith(MockitoExtension.class)
public class ProductSearchControllerTest {

	LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2024, 4, 22, 3, 23, 1);
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ProductSearchService productSearchService;

	@Test
	void 단일_상품_조회() throws Exception {
		// given

		final List<ImageDto> imageDtoList = List.of(
			new ImageDto(1, true, (short)1, TEST_DATE_TIME, TEST_DATE_TIME, "http://image1.com", false),
			new ImageDto(2, false, (short)2, TEST_DATE_TIME, TEST_DATE_TIME, "http://image2.com", false)
		);

		final ProductSearchDto productSearchDto =
			new ProductSearchDto(
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
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				imageDtoList
			);
		// when
		when(productSearchService.getProductById(anyInt())).thenReturn(productSearchDto);
		// then
		mockMvc.perform(get("/api/product-search/v1/1"))
			.andExpect(jsonPath("$.result.id").value(productSearchDto.getId()))
			.andExpect(jsonPath("$.result.category").value(productSearchDto.getCategory().getTitle()))
			.andExpect(jsonPath("$.result.price").value(productSearchDto.getPrice()))
			.andExpect(jsonPath("$.result.stock").value(productSearchDto.getStock()))
			.andExpect(jsonPath("$.result.sellerId").value(productSearchDto.getSellerRep().getId()))
			.andExpect(jsonPath("$.result.sellerName").value(productSearchDto.getSellerRep().getBizName()))
			.andExpect(jsonPath("$.result.favoriteCount").value(productSearchDto.getFavoriteCount()))
			.andExpect(jsonPath("$.result.isDecaf").value(productSearchDto.getIsDecaf()))
			.andExpect(jsonPath("$.result.name").value(productSearchDto.getName()))
			.andExpect(jsonPath("$.result.bean").value(productSearchDto.getBean().getTitle()))
			.andExpect(jsonPath("$.result.acidity").value(productSearchDto.getAcidity().getTitle()))
			.andExpect(jsonPath("$.result.information").value(productSearchDto.getInformation()))
			.andExpect(jsonPath("$.result.status").value(productSearchDto.getStatus().getTitle()))
			.andExpect(jsonPath("$.result.isCrush").value(productSearchDto.getIsCrush()))
			.andExpect(jsonPath("$.result.createDatetime").value(productSearchDto.getCreateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].id").value(imageDtoList.get(0).getId()))
			.andExpect(jsonPath("$.result.imageDtoList[0].isThumbnail").value(imageDtoList.get(0).getIsThumbnail()))
			.andExpect(jsonPath("$.result.imageDtoList[0].sequenceNumber").value(
				Integer.valueOf(imageDtoList.get(0).getSequenceNumber())))
			.andExpect(jsonPath("$.result.imageDtoList[0].createDatetime").value(
				imageDtoList.get(0).getCreateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].updateDatetime").value(
				imageDtoList.get(0).getUpdateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].imageUrl").value(imageDtoList.get(0).getImageUrl()))
			.andExpect(jsonPath("$.result.imageDtoList[1].id").value(imageDtoList.get(1).getId()))
			.andExpect(jsonPath("$.result.imageDtoList[1].isThumbnail").value(imageDtoList.get(1).getIsThumbnail()))
			.andExpect(jsonPath("$.result.imageDtoList[1].sequenceNumber").value(
				Integer.valueOf(imageDtoList.get(1).getSequenceNumber())))
			.andExpect(jsonPath("$.result.imageDtoList[1].createDatetime").value(
				imageDtoList.get(1).getCreateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[1].updateDatetime").value(
				imageDtoList.get(1).getUpdateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[1].imageUrl").value(imageDtoList.get(1).getImageUrl()))
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
