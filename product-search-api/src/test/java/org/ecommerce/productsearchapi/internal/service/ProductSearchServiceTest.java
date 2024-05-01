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
		Product product = getProduct();
		// when
		final ProductSearchDto productSearchDto = productSearchService.saveProduct(product);

		// then
		assertEquals(product.getId(), productSearchDto.getId());
		assertEquals(product.getCategory(), productSearchDto.getCategory());
		assertEquals(product.getPrice(), productSearchDto.getPrice());
		assertEquals(product.getStock(), productSearchDto.getStock());
		assertEquals(product.getSellerRep().getId(), productSearchDto.getSellerRep().getId());
		assertEquals(product.getSellerRep().getBizName(), productSearchDto.getSellerRep().getBizName());
		assertEquals(product.getFavoriteCount(), productSearchDto.getFavoriteCount());
		assertEquals(product.getIsDecaf(), productSearchDto.getIsDecaf());
		assertEquals(product.getName(), productSearchDto.getName());
		assertEquals(product.getBean(), productSearchDto.getBean());
		assertEquals(product.getAcidity(), productSearchDto.getAcidity());
		assertEquals(product.getInformation(), productSearchDto.getInformation());
		assertEquals(getThumbnailUrl(product.getImages()), productSearchDto.getThumbnailUrl());
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

	private String getThumbnailUrl(List<Image> images) {
		return images.stream()
			.filter(Image::getIsThumbnail)
			.findFirst()
			.map(Image::getImageUrl)
			.orElse(null);
	}

}
