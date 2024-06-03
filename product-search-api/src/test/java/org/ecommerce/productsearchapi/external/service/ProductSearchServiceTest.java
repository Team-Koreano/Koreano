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
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;
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
		final ProductDtoWithImageListDto productDtoWithImageListDto = productSearchService.getProductById(1);

		// then
		assertEquals(product.getId(), productDtoWithImageListDto.id());
		assertEquals(product.getCategory(), productDtoWithImageListDto.category());
		assertEquals(product.getPrice(), productDtoWithImageListDto.price());
		assertEquals(product.getStock(), productDtoWithImageListDto.stock());
		assertEquals(product.getSellerRep().getId(), productDtoWithImageListDto.sellerRep().id());
		assertEquals(product.getSellerRep().getBizName(), productDtoWithImageListDto.sellerRep().bizName());
		assertEquals(product.getFavoriteCount(), productDtoWithImageListDto.favoriteCount());
		assertEquals(product.getIsDecaf(), productDtoWithImageListDto.isDecaf());
		assertEquals(product.getName(), productDtoWithImageListDto.name());
		assertEquals(product.getBean(), productDtoWithImageListDto.bean());
		assertEquals(product.getAcidity(), productDtoWithImageListDto.acidity());
		assertEquals(product.getInformation(), productDtoWithImageListDto.information());
		assertEquals(product.getStatus(), productDtoWithImageListDto.status());
		assertEquals(product.getIsCrush(), productDtoWithImageListDto.isCrush());
		assertEquals(product.getCreateDatetime(), productDtoWithImageListDto.createDatetime());
		assertEquals(product.getUpdateDatetime(), productDtoWithImageListDto.updateDatetime());
		assertEquals(product.getImages().get(0).getId(), productDtoWithImageListDto.imageDtoList().get(0).id());
		assertEquals(product.getImages().get(0).getImageUrl(), productDtoWithImageListDto.imageDtoList().get(0).imageUrl());
		assertEquals(product.getImages().get(0).getIsThumbnail(),
			productDtoWithImageListDto.imageDtoList().get(0).isThumbnail());
		assertEquals(product.getImages().get(0).getSequenceNumber(),
			productDtoWithImageListDto.imageDtoList().get(0).sequenceNumber());
		assertEquals(product.getImages().get(0).getIsDeleted(),
			productDtoWithImageListDto.imageDtoList().get(0).isDeleted());
		assertEquals(product.getImages().get(0).getCreateDatetime(),
			productDtoWithImageListDto.imageDtoList().get(0).createDatetime());
		assertEquals(product.getImages().get(0).getUpdateDatetime(),
			productDtoWithImageListDto.imageDtoList().get(0).updateDatetime());
		assertEquals(product.getImages().get(1).getId(), productDtoWithImageListDto.imageDtoList().get(1).id());
		assertEquals(product.getImages().get(1).getImageUrl(), productDtoWithImageListDto.imageDtoList().get(1).imageUrl());
		assertEquals(product.getImages().get(1).getIsThumbnail(),
			productDtoWithImageListDto.imageDtoList().get(1).isThumbnail());
		assertEquals(product.getImages().get(1).getSequenceNumber(),
			productDtoWithImageListDto.imageDtoList().get(1).sequenceNumber());
		assertEquals(product.getImages().get(1).getIsDeleted(),
			productDtoWithImageListDto.imageDtoList().get(1).isDeleted());
		assertEquals(product.getImages().get(1).getCreateDatetime(),
			productDtoWithImageListDto.imageDtoList().get(1).createDatetime());
		assertEquals(product.getImages().get(1).getUpdateDatetime(),
			productDtoWithImageListDto.imageDtoList().get(1).updateDatetime());
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
			(short)1000,
			images
		);
	}

}
