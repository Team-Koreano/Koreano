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
import org.ecommerce.productsearchapi.ControllerTest;
import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.PagedSearchDto;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;
import org.ecommerce.productsearchapi.dto.SellerRepDto;
import org.ecommerce.productsearchapi.dto.request.SearchRequest;
import org.ecommerce.productsearchapi.external.service.ElasticSearchService;
import org.ecommerce.productsearchapi.external.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ProductSearchController.class)
@ExtendWith(MockitoExtension.class)
public class ProductSearchControllerTest extends ControllerTest {

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
				"size",
				"capacity",
				(short)1000
			);
		// when
		when(productSearchService.getProductById(anyInt())).thenReturn(productDtoWithImageListDto);
		// then
		mockMvc.perform(get("/api/external/product/v1/1"))
			.andExpect(jsonPath("$.result.id").value(productDtoWithImageListDto.id()))
			.andExpect(jsonPath("$.result.category").value(productDtoWithImageListDto.category().getTitle()))
			.andExpect(jsonPath("$.result.price").value(productDtoWithImageListDto.price()))
			.andExpect(jsonPath("$.result.stock").value(productDtoWithImageListDto.stock()))
			.andExpect(jsonPath("$.result.sellerId").value(productDtoWithImageListDto.sellerRep().id()))
			.andExpect(jsonPath("$.result.sellerName").value(productDtoWithImageListDto.sellerRep().bizName()))
			.andExpect(jsonPath("$.result.favoriteCount").value(productDtoWithImageListDto.favoriteCount()))
			.andExpect(jsonPath("$.result.isDecaf").value(productDtoWithImageListDto.isDecaf()))
			.andExpect(jsonPath("$.result.name").value(productDtoWithImageListDto.name()))
			.andExpect(jsonPath("$.result.bean").value(productDtoWithImageListDto.bean().getTitle()))
			.andExpect(jsonPath("$.result.acidity").value(productDtoWithImageListDto.acidity().getTitle()))
			.andExpect(jsonPath("$.result.information").value(productDtoWithImageListDto.information()))
			.andExpect(jsonPath("$.result.status").value(productDtoWithImageListDto.status().getTitle()))
			.andExpect(jsonPath("$.result.isCrush").value(productDtoWithImageListDto.isCrush()))
			.andExpect(
				jsonPath("$.result.createDatetime").value(productDtoWithImageListDto.createDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].id").value(imageDtoList.get(0).id()))
			.andExpect(jsonPath("$.result.imageDtoList[0].isThumbnail").value(imageDtoList.get(0).isThumbnail()))
			.andExpect(jsonPath("$.result.imageDtoList[0].sequenceNumber").value(
				Integer.valueOf(imageDtoList.get(0).sequenceNumber())))
			.andExpect(jsonPath("$.result.imageDtoList[0].createDatetime").value(
				imageDtoList.get(0).createDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].updateDatetime").value(
				imageDtoList.get(0).updateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[0].imageUrl").value(imageDtoList.get(0).imageUrl()))
			.andExpect(jsonPath("$.result.imageDtoList[1].id").value(imageDtoList.get(1).id()))
			.andExpect(jsonPath("$.result.imageDtoList[1].isThumbnail").value(imageDtoList.get(1).isThumbnail()))
			.andExpect(jsonPath("$.result.imageDtoList[1].sequenceNumber").value(
				Integer.valueOf(imageDtoList.get(1).sequenceNumber())))
			.andExpect(jsonPath("$.result.imageDtoList[1].createDatetime").value(
				imageDtoList.get(1).createDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[1].updateDatetime").value(
				imageDtoList.get(1).updateDatetime().toString()))
			.andExpect(jsonPath("$.result.imageDtoList[1].imageUrl").value(imageDtoList.get(1).imageUrl()))
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
				new SellerRepDto(1, "커피천국"),
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
				"size",
				"capacity",
				(short)1000
			),
			new ProductDto(
				2,
				ProductCategory.BEAN,
				30000,
				100,
				new SellerRepDto(1, "커피천국"),
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
				"size",
				"capacity",
				(short)1000
			)
		);
		// when
		when(elasticSearchService.suggestSearchKeyword(anyString())).thenReturn(suggestedProducts);
		// then
		mockMvc.perform(get("/api/external/product/v1/suggest?keyword=아메"))
			.andExpect(jsonPath("$.result[0].id").value(suggestedProducts.get(0).id()))
			.andExpect(jsonPath("$.result[0].name").value(suggestedProducts.get(0).name()))
			.andExpect(jsonPath("$.result[1].id").value(suggestedProducts.get(1).id()))
			.andExpect(jsonPath("$.result[1].name").value(suggestedProducts.get(1).name()))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void 상품_리스트_검색() throws Exception {
		// given
		final PagedSearchDto pagedSearchDto = new PagedSearchDto(
			4L,
			5L,
			2,
			3,
			getSearchHits()
		);

		// when
		when(elasticSearchService.searchProducts(any(SearchRequest.class), eq(0), eq(2)))
			.thenReturn(pagedSearchDto);
		// then
		mockMvc.perform(
				get("/api/external/product/v1/search?keyword=아메&category=BEAN&bean=ARABICA&acidity=MEDIUM&sortType=NEWEST&pageNumber=0&pageSize=2"))
			.andExpect(jsonPath("$.result.totalElements").value(pagedSearchDto.totalElements()))
			.andExpect(jsonPath("$.result.totalPages").value(pagedSearchDto.totalPages()))
			.andExpect(jsonPath("$.result.currentPage").value(pagedSearchDto.currentPage()))
			.andExpect(jsonPath("$.result.pageSize").value(pagedSearchDto.pageSize()))
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
			(short)2000,
			images
		);
	}

	private SearchHits<ProductDocument> getSearchHits() {
		ProductDocument productDocument1 = new ProductDocument(1, "BEAN", 30000, 100, 1, "커피천국", 10, false,
			"[특가 EVENT]&아메리카노 원두&세상에서 제일 존맛 커피", "MEDIUM", "ARABICA", "커피천국에서만 만나볼 수 있는 특별한 커피", "http://img123.com",
			"size", "capacity", TEST_DATE_TIME);
		ProductDocument productDocument2 = new ProductDocument(2, "BEAN", 30000, 100, 1, "커피천국", 10, false,
			"아메리카노 아무나 사먹어", "MEDIUM", "ARABICA", "커피천국에서만 만나볼 수 있는 특별한 커피", "http://img123.com", "size", "capacity",
			TEST_DATE_TIME);

		// SearchHit mock 생성
		SearchHit<ProductDocument> searchHit1 = new SearchHit<>("1", "1", null, 1.0f, null, null, null, null, null,
			null, productDocument1);
		SearchHit<ProductDocument> searchHit2 = new SearchHit<>("2", "2", null, 2.0f, null, null, null, null, null,
			null, productDocument2);

		return new SearchHitsImpl<>(1L, TotalHitsRelation.EQUAL_TO, 1.0f, null, null, List.of(searchHit1, searchHit2),
			null, null);
	}

}
