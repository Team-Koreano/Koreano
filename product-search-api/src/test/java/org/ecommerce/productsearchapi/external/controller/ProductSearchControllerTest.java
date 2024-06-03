package org.ecommerce.productsearchapi.external.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.ecommerce.productsearchapi.dto.request.SearchRequest;
import org.ecommerce.productsearchapi.external.service.ElasticSearchService;
import org.ecommerce.productsearchapi.external.service.ProductSearchService;
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
	@MockBean
	private ElasticSearchService elasticSearchService;

	@Test
	void 단일_상품_조회() throws Exception {
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
				null,
				"size",
				"capacity"
			);
		// when
		when(productSearchService.getProductById(anyInt())).thenReturn(productDto);
		// then
		mockMvc.perform(get("/api/external/product/v1/1"))
			.andExpect(jsonPath("$.result.id").value(productDto.getId()))
			.andExpect(jsonPath("$.result.category").value(productDto.getCategory().getTitle()))
			.andExpect(jsonPath("$.result.price").value(productDto.getPrice()))
			.andExpect(jsonPath("$.result.stock").value(productDto.getStock()))
			.andExpect(jsonPath("$.result.sellerId").value(productDto.getSellerRep().getId()))
			.andExpect(jsonPath("$.result.sellerName").value(productDto.getSellerRep().getBizName()))
			.andExpect(jsonPath("$.result.favoriteCount").value(productDto.getFavoriteCount()))
			.andExpect(jsonPath("$.result.isDecaf").value(productDto.getIsDecaf()))
			.andExpect(jsonPath("$.result.name").value(productDto.getName()))
			.andExpect(jsonPath("$.result.bean").value(productDto.getBean().getTitle()))
			.andExpect(jsonPath("$.result.acidity").value(productDto.getAcidity().getTitle()))
			.andExpect(jsonPath("$.result.information").value(productDto.getInformation()))
			.andExpect(jsonPath("$.result.status").value(productDto.getStatus().getTitle()))
			.andExpect(jsonPath("$.result.isCrush").value(productDto.getIsCrush()))
			.andExpect(jsonPath("$.result.createDatetime").value(productDto.getCreateDatetime().toString()))
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

	@Test
	void 검색어_제안() throws Exception {
		// given
		final List<ProductDto> suggestedProducts = List.of(
			new ProductDto(
				1,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductDto.SellerRep(1, "커피천국"),
				10,
				false,
				"아메리카노",
				Bean.ARABICA,
				Acidity.MEDIUM,
				"커피천국에서만 만나볼 수 있는 특별한 커피",
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				null,
				null,
				"size",
				"capacity"
			),
			new ProductDto(
				2,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductDto.SellerRep(1, "커피천국"),
				10,
				false,
				"아메아메아메",
				Bean.ARABICA,
				Acidity.MEDIUM,
				"커피천국에서만 만나볼 수 있는 특별한 커피",
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				null,
				null,
				"size",
				"capacity"
			)
		);
		// when
		when(elasticSearchService.suggestSearchKeyword(anyString())).thenReturn(suggestedProducts);
		// then
		mockMvc.perform(get("/api/external/product/v1/suggest?keyword=아메"))
			.andExpect(jsonPath("$.result[0].id").value(suggestedProducts.get(0).getId()))
			.andExpect(jsonPath("$.result[0].name").value(suggestedProducts.get(0).getName()))
			.andExpect(jsonPath("$.result[1].id").value(suggestedProducts.get(1).getId()))
			.andExpect(jsonPath("$.result[1].name").value(suggestedProducts.get(1).getName()))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void 상품_리스트_검색() throws Exception {
		// given
		final List<ProductDto> searchDtoList = List.of(
			new ProductDto(
				1,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductDto.SellerRep(1, "커피천국"),
				10,
				false,
				"아메리카노",
				Bean.ARABICA,
				Acidity.MEDIUM,
				"커피천국에서만 만나볼 수 있는 특별한 커피",
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				null,
				null,
				"size",
				"capacity"
			),
			new ProductDto(
				2,
				ProductCategory.BEAN,
				30000,
				100,
				new ProductDto.SellerRep(1, "커피천국"),
				10,
				false,
				"아메아메아메",
				Bean.ARABICA,
				Acidity.MEDIUM,
				"커피천국에서만 만나볼 수 있는 특별한 커피",
				ProductStatus.AVAILABLE,
				false,
				TEST_DATE_TIME,
				TEST_DATE_TIME,
				null,
				null,
				"size",
				"capacity"
			)
		);

		// when
		when(elasticSearchService.searchProducts(any(SearchRequest.class), eq(0), eq(2)))
			.thenReturn(searchDtoList);
		// then
		mockMvc.perform(get("/api/external/product/v1/search?keyword=아메&category=BEAN&bean=ARABICA&acidity=MEDIUM&sortType=NEWEST&pageNumber=0&pageSize=2"))
			.andExpect(jsonPath("$.result[0].id").value(searchDtoList.get(0).id()))
			.andExpect(jsonPath("$.result[0].name").value(searchDtoList.get(0).name()))
			.andExpect(jsonPath("$.result[0].favoriteCount").value(searchDtoList.get(1).favoriteCount()))
			.andExpect(jsonPath("$.result[1].id").value(searchDtoList.get(1).id()))
			.andExpect(jsonPath("$.result[1].name").value(searchDtoList.get(1).name()))
			.andExpect(jsonPath("$.result[1].favoriteCount").value(searchDtoList.get(1).favoriteCount()))
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
