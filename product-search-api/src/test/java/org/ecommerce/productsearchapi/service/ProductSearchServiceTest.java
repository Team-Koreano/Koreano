package org.ecommerce.productsearchapi.service;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
import org.ecommerce.productsearchapi.repository.querydsl.ProductCustomRepositoryImpl;
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
	private ProductCustomRepositoryImpl productCustomRepository;

	@Test
	void 단일_상품_조회() {
		// given
		final List<ImageDto> imageDtoList = List.of(
			new ImageDto(1, true, (short)1, TEST_DATE_TIME, TEST_DATE_TIME, "http://image1.com"),
			new ImageDto(2, false, (short)2, TEST_DATE_TIME, TEST_DATE_TIME, "http://image2.com")
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
		given(productCustomRepository.findProductById(anyInt())).willReturn(Optional.of(productSearchDto));

		// when
		final ProductSearchDto product = productSearchService.getProductById(1);

		// then
		assertEquals(1, product.getId());
		assertEquals(ProductCategory.BEAN, product.getCategory());
		assertEquals(30000, product.getPrice());
		assertEquals(100, product.getStock());
		assertEquals(1, product.getSellerRep().getId());
		assertEquals("커피천국", product.getSellerRep().getBizName());
		assertEquals(10, product.getFavoriteCount());
		assertEquals(false, product.getIsDecaf());
		assertEquals("[특가 EVENT]&아라비카 원두&세상에서 제일 존맛 커피", product.getName());
		assertEquals(Bean.ARABICA, product.getBean());
		assertEquals(Acidity.MEDIUM, product.getAcidity());
		assertEquals("커피천국에서만 만나볼 수 있는 특별한 커피", product.getInformation());
		assertEquals(ProductStatus.AVAILABLE, product.getStatus());
		assertEquals(false, product.getIsCrush());
		assertEquals(TEST_DATE_TIME, product.getCreateDateTime());
		assertEquals(TEST_DATE_TIME, product.getUpdateDateTime());
		assertEquals(imageDtoList, product.getImageDtoList());
	}

	@Test
	void 없는_상품_조회() {
		// given
		given(productCustomRepository.findProductById(anyInt())).willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class, () -> productSearchService.getProductById(1));

		//then
		assertEquals(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID, exception.getErrorCode());
	}

}
