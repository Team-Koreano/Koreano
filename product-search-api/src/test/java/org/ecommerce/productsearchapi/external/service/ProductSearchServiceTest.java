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
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
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

		given(productRepository.findProductById(anyInt())).willReturn(Optional.of(product));

		// when
		final ProductSearchDto productSearchDto = productSearchService.getProductById(1);

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
		assertEquals(product.getStatus(), productSearchDto.getStatus());
		assertEquals(product.getIsCrush(), productSearchDto.getIsCrush());
		assertEquals(product.getCreateDatetime(), productSearchDto.getCreateDatetime());
		assertEquals(product.getUpdateDatetime(), productSearchDto.getUpdateDatetime());
		assertEquals(product.getImages().get(0).getId(), productSearchDto.getImageDtoList().get(0).getId());
		assertEquals(product.getImages().get(0).getImageUrl(), productSearchDto.getImageDtoList().get(0).getImageUrl());
		assertEquals(product.getImages().get(0).getIsThumbnail(), productSearchDto.getImageDtoList().get(0).getIsThumbnail());
		assertEquals(product.getImages().get(0).getSequenceNumber(), productSearchDto.getImageDtoList().get(0).getSequenceNumber());
		assertEquals(product.getImages().get(0).getIsDeleted(), productSearchDto.getImageDtoList().get(0).getIsDeleted());
		assertEquals(product.getImages().get(0).getCreateDatetime(), productSearchDto.getImageDtoList().get(0).getCreateDatetime());
		assertEquals(product.getImages().get(0).getUpdateDatetime(), productSearchDto.getImageDtoList().get(0).getUpdateDatetime());
		assertEquals(product.getImages().get(1).getId(), productSearchDto.getImageDtoList().get(1).getId());
		assertEquals(product.getImages().get(1).getImageUrl(), productSearchDto.getImageDtoList().get(1).getImageUrl());
		assertEquals(product.getImages().get(1).getIsThumbnail(), productSearchDto.getImageDtoList().get(1).getIsThumbnail());
		assertEquals(product.getImages().get(1).getSequenceNumber(), productSearchDto.getImageDtoList().get(1).getSequenceNumber());
		assertEquals(product.getImages().get(1).getIsDeleted(), productSearchDto.getImageDtoList().get(1).getIsDeleted());
		assertEquals(product.getImages().get(1).getCreateDatetime(), productSearchDto.getImageDtoList().get(1).getCreateDatetime());
		assertEquals(product.getImages().get(1).getUpdateDatetime(), productSearchDto.getImageDtoList().get(1).getUpdateDatetime());
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
