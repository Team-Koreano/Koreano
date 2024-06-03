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
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;
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
		final ProductDtoWithImageListDto productDtoWithImageListDto = productSearchService.saveProduct(product);

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
		assertEquals(getThumbnailUrl(product.getImages()), productDtoWithImageListDto.thumbnailUrl());
		assertEquals(TEST_DATE_TIME, productDtoWithImageListDto.createDatetime());
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

	private String getThumbnailUrl(List<Image> images) {
		return images.stream()
			.filter(Image::getIsThumbnail)
			.findFirst()
			.map(Image::getImageUrl)
			.orElse(null);
	}

}
