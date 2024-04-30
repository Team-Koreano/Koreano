package org.ecommerce.productsearchapi.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.ecommerce.productsearchapi.repository.ProductRepository;
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
	private ProductRepository productRepository;

	@Test
	void 단일_상품_조회() {
		
		// given
		final Product product = getProduct();

		given(productRepository.findProductById(anyInt())).willReturn(Optional.of(product));

		// when
		final ProductSearchDto productSearchDto = productSearchService.getProductById(1);

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
		assertEquals(ProductStatus.AVAILABLE, productSearchDto.getStatus());
		assertEquals(false, productSearchDto.getIsCrush());
		assertEquals(TEST_DATE_TIME, productSearchDto.getCreateDatetime());
		assertEquals(TEST_DATE_TIME, productSearchDto.getUpdateDatetime());
		assertEquals(1, productSearchDto.getImageDtoList().get(0).getId());
		assertEquals("http://image1.com", productSearchDto.getImageDtoList().get(0).getImageUrl());
		assertEquals(true, productSearchDto.getImageDtoList().get(0).getIsThumbnail());
		assertEquals((short)1, productSearchDto.getImageDtoList().get(0).getSequenceNumber());
		assertEquals(false, productSearchDto.getImageDtoList().get(0).getIsDeleted());
		assertEquals(TEST_DATE_TIME, productSearchDto.getImageDtoList().get(0).getCreateDatetime());
		assertEquals(TEST_DATE_TIME, productSearchDto.getImageDtoList().get(0).getUpdateDatetime());
		assertEquals(2, productSearchDto.getImageDtoList().get(1).getId());
		assertEquals("http://image2.com", productSearchDto.getImageDtoList().get(1).getImageUrl());
		assertEquals(false, productSearchDto.getImageDtoList().get(1).getIsThumbnail());
		assertEquals((short)2, productSearchDto.getImageDtoList().get(1).getSequenceNumber());
		assertEquals(false, productSearchDto.getImageDtoList().get(1).getIsDeleted());
		assertEquals(TEST_DATE_TIME, productSearchDto.getImageDtoList().get(1).getCreateDatetime());
		assertEquals(TEST_DATE_TIME, productSearchDto.getImageDtoList().get(1).getUpdateDatetime());
	}

	@Test
	void 없는_상품_조회() {
		// given
		given(productRepository.findProductById(anyInt())).willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class, () -> productSearchService.getProductById(1));

		//then
		assertEquals(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID, exception.getErrorCode());
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
