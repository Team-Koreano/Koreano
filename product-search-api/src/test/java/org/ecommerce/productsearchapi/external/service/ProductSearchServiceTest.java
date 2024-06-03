package org.ecommerce.productsearchapi.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
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

		given(productRepository.findProductById(anyInt())).willReturn(product);

		// when
		final ProductDto productDto = productSearchService.getProductById(1);

		// then
		assertEquals(product.getId(), productDto.getId());
		assertEquals(product.getCategory(), productDto.getCategory());
		assertEquals(product.getPrice(), productDto.getPrice());
		assertEquals(product.getStock(), productDto.getStock());
		assertEquals(product.getSellerRep().getId(), productDto.getSellerRep().getId());
		assertEquals(product.getSellerRep().getBizName(), productDto.getSellerRep().getBizName());
		assertEquals(product.getFavoriteCount(), productDto.getFavoriteCount());
		assertEquals(product.getIsDecaf(), productDto.getIsDecaf());
		assertEquals(product.getName(), productDto.getName());
		assertEquals(product.getBean(), productDto.getBean());
		assertEquals(product.getAcidity(), productDto.getAcidity());
		assertEquals(product.getInformation(), productDto.getInformation());
		assertEquals(product.getStatus(), productDto.getStatus());
		assertEquals(product.getIsCrush(), productDto.getIsCrush());
		assertEquals(product.getCreateDatetime(), productDto.getCreateDatetime());
		assertEquals(product.getUpdateDatetime(), productDto.getUpdateDatetime());
		assertEquals(product.getImages().get(0).getId(), productDto.getImageDtoList().get(0).getId());
		assertEquals(product.getImages().get(0).getImageUrl(), productDto.getImageDtoList().get(0).getImageUrl());
		assertEquals(product.getImages().get(0).getIsThumbnail(),
			productDto.getImageDtoList().get(0).getIsThumbnail());
		assertEquals(product.getImages().get(0).getSequenceNumber(),
			productDto.getImageDtoList().get(0).getSequenceNumber());
		assertEquals(product.getImages().get(0).getIsDeleted(),
			productDto.getImageDtoList().get(0).getIsDeleted());
		assertEquals(product.getImages().get(0).getCreateDatetime(),
			productDto.getImageDtoList().get(0).getCreateDatetime());
		assertEquals(product.getImages().get(0).getUpdateDatetime(),
			productDto.getImageDtoList().get(0).getUpdateDatetime());
		assertEquals(product.getImages().get(1).getId(), productDto.getImageDtoList().get(1).getId());
		assertEquals(product.getImages().get(1).getImageUrl(), productDto.getImageDtoList().get(1).getImageUrl());
		assertEquals(product.getImages().get(1).getIsThumbnail(),
			productDto.getImageDtoList().get(1).getIsThumbnail());
		assertEquals(product.getImages().get(1).getSequenceNumber(),
			productDto.getImageDtoList().get(1).getSequenceNumber());
		assertEquals(product.getImages().get(1).getIsDeleted(),
			productDto.getImageDtoList().get(1).getIsDeleted());
		assertEquals(product.getImages().get(1).getCreateDatetime(),
			productDto.getImageDtoList().get(1).getCreateDatetime());
		assertEquals(product.getImages().get(1).getUpdateDatetime(),
			productDto.getImageDtoList().get(1).getUpdateDatetime());
	}

	@Test
	void 없는_상품_조회() {
		// given
		given(productRepository.findProductById(anyInt())).willReturn(null);

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
