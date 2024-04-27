package org.ecommerce.productmanagementapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProductManagementServiceTest {

	private static final LocalDateTime testTime = LocalDateTime.
		parse("2024-04-14T17:41:52+09:00",
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
	private static final SellerRep test = new SellerRep(1, "TEST");
	@InjectMocks
	private ProductManagementService productManagementService;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ImageRepository imageRepository;

	@Test
	void 상품_등록() {
		final List<ProductManagementDto.Request.Register.ImageDto> imageDtos = List.of(
			new ProductManagementDto.Request.Register.ImageDto("image1.jpg", true, (short)1),
			new ProductManagementDto.Request.Register.ImageDto("image2.jpg", false, (short)2),
			new ProductManagementDto.Request.Register.ImageDto("image3.jpg", false, (short)3)
		);

		final ProductManagementDto.Request.Register productDtos =
			new ProductManagementDto.Request.Register(
				true,
				1000,
				50,
				Acidity.CINNAMON,
				Bean.ARABICA,
				ProductCategory.BEAN,
				"정말 맛있는 원두 단돈 천원",
				"부산 진구 유명가수가 좋아하는 원두",
				false,
				imageDtos
			);

		final Product product = Product.ofCreate(
			productDtos.category(),
			productDtos.price(),
			productDtos.stock(),
			productDtos.name(),
			productDtos.bean(),
			productDtos.acidity(),
			productDtos.information(),
			productDtos.isCrush(),
			productDtos.isDecaf(),
			test
		);

		given(productRepository.save(any(Product.class))).willReturn(
			product
		);
		final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

		final ProductManagementDto productManagementDto = productManagementService.productRegister(productDtos);

		verify(productRepository, times(1)).save(captor.capture());
		verify(imageRepository, times(1)).saveAll(anyList());

		assertThat(productManagementDto.getAcidity()).isEqualTo(captor.getValue().getAcidity());
		assertThat(productManagementDto.getBean()).isEqualTo(captor.getValue().getBean());
		assertThat(productManagementDto.getCategory()).isEqualTo(captor.getValue().getCategory());
		assertThat(productManagementDto.getInformation()).isEqualTo(captor.getValue().getInformation());
		assertThat(productManagementDto.getName()).isEqualTo(captor.getValue().getName());
		assertThat(productManagementDto.getPrice()).isEqualTo(captor.getValue().getPrice());
		assertThat(productManagementDto.getStock()).isEqualTo(captor.getValue().getStock());
		assertThat(productManagementDto.getSellerRep()).usingRecursiveComparison()
			.isEqualTo(captor.getValue().getSellerRep());
		assertThat(productManagementDto.getIsCrush()).isEqualTo(captor.getValue().getIsCrush());
		assertThat(productManagementDto.getIsDecaf()).isEqualTo(captor.getValue().getIsDecaf());
	}



	@Nested
	class 상품_상태_변경 {
		@Test
		void 상품_상태_변경_성공() {

			final Integer productId = 1;

			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, 50, test, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, ProductStatus.AVAILABLE, testTime, testTime, null
			);
			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			entity.toModifyStatus(newStatus);

			ProductManagementDto result = productManagementService.modifyToStatus(productId, newStatus);

			assertThat(result.getStatus()).isEqualTo(newStatus);
		}
	}

	@Nested
	class 상품_재고_변경 {
		@Test
		void 상품_재고_변경_성공() {

			final Integer productId = 1;

			final Integer stock = 20;

			final Integer existStock = 50;

			ProductManagementDto.Request.Stock request = new ProductManagementDto.Request.Stock(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, test, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, ProductStatus.AVAILABLE, testTime, testTime, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			ProductManagementDto result = productManagementService.modifyToStock(request);

			assertThat(result.getStock()).isEqualTo(existStock + stock);
		}

		@Test
		void 상품_재고_변경_실패() {

			final Integer productId = 1;

			final Integer stock = -40;

			final Integer existStock = 30;

			ProductManagementDto.Request.Stock request = new ProductManagementDto.Request.Stock(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, test, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, ProductStatus.AVAILABLE, testTime, testTime, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			assertThatThrownBy(() -> productManagementService.modifyToStock(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductManagementErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO.getMessage());
		}
	}
	@Test
	void 상품_수정() {
		final Integer productId = 1;

		final ProductManagementDto.Request.Modify dto = new ProductManagementDto.Request.Modify(
			true, 10000, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN,
			"수정된", "커피", true);

		final Product entity = new Product(
			productId, ProductCategory.BEAN, 1000, 30, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, ProductStatus.AVAILABLE, testTime, testTime, null
		);
		when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

		ProductManagementDto resultDto = productManagementService.modifyToProduct(productId, dto);

		verify(productRepository).findById(productId);

		assertThat(resultDto.getAcidity()).isEqualTo(dto.acidity());
		assertThat(resultDto.getBean()).isEqualTo(dto.bean());
		assertThat(resultDto.getCategory()).isEqualTo(dto.category());
		assertThat(resultDto.getInformation()).isEqualTo(dto.information());
		assertThat(resultDto.getName()).isEqualTo(dto.name());
		assertThat(resultDto.getPrice()).isEqualTo(dto.price());
		assertThat(resultDto.getIsCrush()).isEqualTo(dto.isCrush());
		assertThat(resultDto.getIsDecaf()).isEqualTo(dto.isDecaf());
	}
}
