package org.ecommerce.productsearchapi.internal.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductSearchServiceTest {

	LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2024, 4, 22, 3, 23, 1);
	@InjectMocks
	private ProductSearchService productSearchService;
	@Mock
	private ProductElasticsearchRepository productElasticsearchRepository;

	@Test
	void 엘라스틱서치에_상품_정보_저장() {
		// given

		// when
		final ProductSearchDto productSearchDto = productSearchService.saveProduct(getProduct());

		// then
		assertEquals(1, productSearchDto.getId());
		assertEquals(ProductCategory.BEAN, productSearchDto.getCategory());
		assertEquals(30000, productSearchDto.getPrice());
		assertEquals(100, productSearchDto.getStock());
		assertEquals(1, productSearchDto.getSellerRep().getId());
		assertEquals("커피천국", productSearchDto.getSellerRep().getBizName());
		assertEquals(10, productSearchDto.getFavoriteCount());
		assertEquals(false, productSearchDto.getIsDecaf());
		assertEquals("[특가 EVENT]&아라비카 원두&세상에서 제일 존맛 커피", productSearchDto.getName());
		assertEquals(Bean.ARABICA, productSearchDto.getBean());
		assertEquals(Acidity.MEDIUM, productSearchDto.getAcidity());
		assertEquals("커피천국에서만 만나볼 수 있는 특별한 커피", productSearchDto.getInformation());
		assertEquals("http://image1.com", productSearchDto.getThumbnailUrl());
		assertEquals(TEST_DATE_TIME, productSearchDto.getCreateDatetime());
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
